package ir.mkhakpaki.videogames.ui.favorites

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

class FavoritesViewModel
@Inject constructor(private val repository: GameRepository) : ViewModel() {

    private val _stateViewLiveData = MutableLiveData<ViewStateModel>()
    val stateViewLiveData: LiveData<ViewStateModel> = _stateViewLiveData

    private val _itemsLiveData = MutableLiveData<List<GameItem>>()
    val itemsLiveData: LiveData<List<GameItem>> = _itemsLiveData

    private val items = mutableListOf<GameItem>()

    var possibleError: ErrorModel? = null
        private set

    init {
        viewModelScope.launch {
            repository.flowGames.collect { response ->
                when (response) {
                    is RepoResponse.Error -> handleDataError(response.error)
                    is RepoResponse.Data -> {
                        processListItems(response.data.games)
                    }
                }
            }
        }
    }

    fun loadLikedGames() {
        if(items.isEmpty()) {
            _stateViewLiveData.value = ViewStateModel.LOADING
        }
        viewModelScope.launch {
            repository.getLikedGames()
        }
    }


    private fun handleDataError(error: ErrorModel) {
        possibleError = error
        if (items.isEmpty()) {
            _stateViewLiveData.value = ViewStateModel.ERROR
            return
        }
    }

    private suspend fun processListItems(games: MutableList<GameModel>) {
        withContext(Dispatchers.Default) {
            if (games.isEmpty()) {
                _stateViewLiveData.postValue(ViewStateModel.EMPTY)
                return@withContext
            }
            items.clear()
            items.addAll(games.map {
                GameItem(
                    type = Constants.TYPE_GAME_ITEM_LIST,
                    id = it.gameId,
                    game = it
                )
            })
            _stateViewLiveData.postValue(ViewStateModel.DATA)
            _itemsLiveData.postValue(items.toMutableList())
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