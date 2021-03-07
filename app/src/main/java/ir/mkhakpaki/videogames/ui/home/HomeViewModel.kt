package ir.mkhakpaki.videogames.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.DiffUtil
import ir.mkhakpaki.videogames.repository.GameRepository
import ir.mkhakpaki.videogames.ui.model.*
import ir.mkhakpaki.videogames.util.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class HomeViewModel
@Inject constructor(private val repository: GameRepository) : ViewModel() {

    private val _stateViewLiveData = MutableLiveData<ViewStateModel>()
    val stateViewLiveData: LiveData<ViewStateModel> = _stateViewLiveData

    private val _itemsLiveData = MutableLiveData<List<GameItem>>()
    val itemsLiveData: LiveData<List<GameItem>> = _itemsLiveData

    private val loadingItem = GameItem.makeLoadingItem()
    private val errorItem = GameItem.makeErrorItem()
    private var isFetchingItems = false
    private var nextPage: Int? = 2
    private var isEndOfList = false
    private val items = mutableListOf<GameItem>()
    var possibleError: ErrorModel? = null
        private set

    init {
        viewModelScope.launch {
            repository.flowGames.collect { response ->
                when (response) {
                    is RepoResponse.Error -> handleDataError(response.error)
                    is RepoResponse.Data -> {
                        val data = response.data
                        isFetchingItems = false
                        _stateViewLiveData.value = ViewStateModel.DATA
                        data.nextPage?.let {
                            nextPage = it
                        }

                        checkEndOfList(data.ended)
                        prepareListItems(response.data.games)
                    }
                }
            }
        }
        viewModelScope.launch {
            repository.getAllGames(null)
        }
    }

    private suspend fun prepareListItems(games: MutableList<GameModel>) {
        withContext(Dispatchers.Default) {
            items.clear()
            items.addAll(games.map {
                GameItem(
                    type = Constants.TYPE_GAME_ITEM_LIST,
                    id = it.gameId,
                    game = it
                )
            })

            if (!isEndOfList) {
                items.add(loadingItem)
            }

            _itemsLiveData.postValue(items.toMutableList())
        }
    }

    private fun checkEndOfList(ended: Boolean?) {
        isEndOfList = ended == true
        if (isEndOfList) {
            _stateViewLiveData.value = ViewStateModel.LIST_END
        }
    }

    private fun handleDataError(error: ErrorModel) {
        possibleError = error
        isFetchingItems = false
        if (items.isEmpty()) {
            _stateViewLiveData.value = ViewStateModel.ERROR
            return
        }
        items.remove(loadingItem)
        if (items.last().itemType != Constants.VIEW_TYPE_ERROR) {
            items.add(errorItem)
        }
        _itemsLiveData.value = items.toMutableList()
    }

    fun loadMore() {
        if (isFetchingItems || isEndOfList) return
        isFetchingItems = true
        viewModelScope.launch {
            repository.requestGameList(nextPage)
        }
    }

    fun tryLoadMore() {
        isFetchingItems = false
        items.remove(errorItem)
        items.add(loadingItem)
        _itemsLiveData.value = items.toMutableList()
        loadMore()
    }

    val gamesDiff = object : DiffUtil.ItemCallback<GameItem>() {
        override fun areItemsTheSame(
            oldGame: GameItem,
            newGame: GameItem
        ): Boolean {
            return oldGame.id == newGame.id
        }

        override fun areContentsTheSame(
            oldGame: GameItem,
            newGame: GameItem
        ): Boolean {
            return oldGame == newGame
        }
    }

}