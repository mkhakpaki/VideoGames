package ir.mkhakpaki.videogames.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.DiffUtil
import com.orhanobut.hawk.Hawk
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

    private val _searchItemsLiveData = MutableLiveData<List<GameItem>>()
    val searchItemsLiveData: LiveData<List<GameItem>> = _searchItemsLiveData

    private val loadingItem = GameItem.makeLoadingItem()
    private val errorItem = GameItem.makeErrorItem()
    private var isFetchingItems = false
    private var nextPage: Int? = 2
    private var isEndOfList = false
    private val items = mutableListOf<GameItem>()
    var possibleError: ErrorModel? = null
        private set

    init {
        nextPage = Hawk.get(Constants.NEXT_PAGE_TO_REQUEST, 2)
        viewModelScope.launch {
            repository.flowGames.collect { response ->
                when (response) {
                    is RepoResponse.Error -> handleDataError(response.error)
                    is RepoResponse.Data -> {
                        val data = response.data
                        if (data.isSearchMode == true) {
                            if (data.games.isNullOrEmpty()) {
                                _stateViewLiveData.postValue(ViewStateModel.EMPTY)
                                return@collect
                            }
                            handleSearchResult(data)
                        } else {
                            isFetchingItems = false
                            _stateViewLiveData.value = ViewStateModel.DATA
                            data.nextPage?.let {
                                nextPage = it
                            }

                            checkEndOfList(data.ended)
                            processListItems(data.games)
                        }
                    }
                }
            }
        }
        viewModelScope.launch {
            _stateViewLiveData.value = ViewStateModel.LOADING
            repository.getAllGames()
        }
    }

    private suspend fun handleSearchResult(data: GameListModel) {
        withContext(Dispatchers.Default) {
            _stateViewLiveData.postValue(ViewStateModel.SEARCH)
            _searchItemsLiveData.postValue(data.games.map {
                GameItem(
                    type = Constants.TYPE_GAME_ITEM_LIST,
                    id = it.gameId,
                    game = it
                )
            }.toMutableList())
        }
    }

    fun closeSearch() {
        _stateViewLiveData.value = ViewStateModel.DATA
        _itemsLiveData.value = items.toMutableList()
    }

    private suspend fun processListItems(games: MutableList<GameModel>) {
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

    fun refresh() {
        if (isFetchingItems)
            return
        _stateViewLiveData.value = ViewStateModel.LOADING
        viewModelScope.launch {
            repository.refresh()
        }
    }

    fun searchGames(query: String) {
        viewModelScope.launch {
            repository.searchGames(query)
        }
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