package ir.mkhakpaki.videogames.ui.gameDetails

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import ir.mkhakpaki.videogames.repository.GameRepository
import ir.mkhakpaki.videogames.ui.model.ErrorModel
import ir.mkhakpaki.videogames.ui.model.GameModel
import ir.mkhakpaki.videogames.ui.model.RepoResponse
import ir.mkhakpaki.videogames.ui.model.ViewStateModel
import ir.mkhakpaki.videogames.util.Constants
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class GameDetailsViewModel
@Inject constructor(private val repository: GameRepository) : ViewModel() {

    private val _stateViewLiveData = MutableLiveData<ViewStateModel>()
    val stateViewLiveData: LiveData<ViewStateModel> = _stateViewLiveData

    private val _itemLiveData = MutableLiveData<GameModel>()
    val itemLiveData: LiveData<GameModel> = _itemLiveData

    private var item: GameModel? = null

    private var gameId: Long = 0

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

    fun setGameId(id: Long) {
        logOpenGameEvent(id)
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

    fun toggleLike(): Boolean {
        viewModelScope.launch {
            repository.toggleLike(gameId)
        }
        item?.isLiked = !(item?.isLiked ?: false)
        return item?.isLiked ?: false
    }

    private fun logOpenGameEvent(id: Long) {
        val params = Bundle()
        params.putLong(Constants.GAME_ID, id)
        Firebase.analytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM, params)
    }
}