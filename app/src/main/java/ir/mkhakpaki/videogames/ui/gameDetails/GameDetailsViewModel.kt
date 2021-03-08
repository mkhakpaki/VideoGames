package ir.mkhakpaki.videogames.ui.gameDetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.mkhakpaki.videogames.repository.GameRepository
import ir.mkhakpaki.videogames.ui.model.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class GameDetailsViewModel
@Inject constructor(private val repository: GameRepository) : ViewModel() {

    private val _stateViewLiveData = MutableLiveData<ViewStateModel>()
    val stateViewLiveData: LiveData<ViewStateModel> = _stateViewLiveData

    private val _itemLiveData = MutableLiveData<GameModel>()
    val itemLiveData : LiveData<GameModel> = _itemLiveData

    private var item:GameModel? = null

    private var gameId:Long = 0

    var possibleError: ErrorModel? = null
        private set

    init {
        viewModelScope.launch {
            repository.flowGames.collect { response ->
                when (response) {
                    is RepoResponse.Error -> handleDataError(response.error)
                    is RepoResponse.Data -> {
                        val data = response.data
                        item = data.games.first()
                        _itemLiveData.postValue(item?.copy())
                        _stateViewLiveData.value = ViewStateModel.DATA
                    }
                }
            }
        }
    }

    fun setGameId(id:Long) {
        gameId = id
        _stateViewLiveData.value = ViewStateModel.LOADING
        viewModelScope.launch {
            repository.getGameDetail(id)
        }
    }

    private fun handleDataError(error: ErrorModel) {
        possibleError = error
        _stateViewLiveData.value = ViewStateModel.ERROR
    }

    fun toggleLike():Boolean {
        viewModelScope.launch {
            repository.toggleLike(gameId)
        }
        item?.isLiked = !(item?.isLiked ?: false)
        return item?.isLiked ?: false
    }
}