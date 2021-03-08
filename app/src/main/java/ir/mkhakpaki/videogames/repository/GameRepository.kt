package ir.mkhakpaki.videogames.repository

import android.util.Log
import ir.mkhakpaki.videogames.db.GameDao
import ir.mkhakpaki.videogames.db.GameEntity
import ir.mkhakpaki.videogames.network.NetworkHelper
import ir.mkhakpaki.videogames.network.NetworkResult
import ir.mkhakpaki.videogames.network.pojo.GameListPojo
import ir.mkhakpaki.videogames.network.pojo.GamePojo
import ir.mkhakpaki.videogames.ui.model.ErrorModel
import ir.mkhakpaki.videogames.ui.model.GameListModel
import ir.mkhakpaki.videogames.ui.model.GameModel
import ir.mkhakpaki.videogames.ui.model.RepoResponse
import ir.mkhakpaki.videogames.util.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GameRepository @Inject constructor(
    private val networkHelper: NetworkHelper,
    private val gameDao: GameDao
) {

    private val channelGames = Channel<RepoResponse<GameListModel, ErrorModel>>()
    val flowGames: Flow<RepoResponse<GameListModel, ErrorModel>> = channelGames.consumeAsFlow()

    suspend fun getAllGames(page: Int?) {
        getGamesFromDB(null, null)
        requestGameList(page)
    }

    private suspend fun getGamesFromDB(ended:Boolean?, nextPage: Int?) {
        withContext(Dispatchers.IO) {
            val dbGames = gameDao.getAll()
            if (dbGames.isNotEmpty()) {
                channelGames.send(
                    RepoResponse.Data(
                        GameListModel(
                            ended = ended,
                            nextPage = nextPage,
                            games = dbGames.map { GameModel(it) }.toMutableList()
                        )
                    )
                )
            }
        }
    }

    suspend fun requestGameList(page: Int?) {
        withContext(Dispatchers.IO) {
            val result = networkHelper.listGames(page)
            when (result) {
                is NetworkResult.Success -> {
                    val gameLisPojo = result.data
                    storeGames(gameLisPojo, page ?: 1)
                    getGamesFromDB(gameLisPojo.nextPage == null, extractNextPage(gameLisPojo.nextPage))
                }
                is NetworkResult.Error -> {
                    channelGames.send(
                        RepoResponse.Error(ErrorModel(code = result.error.code, message = result.error.message))
                    )
                }
                is NetworkResult.Failure -> {
                    channelGames.send(
                        RepoResponse.Error(ErrorModel(exception = result.exception))
                    )
                }
            }

        }
    }

    private fun extractNextPage(nextPage: String?): Int? {
        nextPage?.substringAfter("page=")?.let {
            return it.toInt()
        }
        return null
    }

    private fun storeGames(gameListPojo: GameListPojo, page: Int) {
        if (page == 1) {
            gameDao.clearGames()
        }
        gameListPojo.gameList?.let { gameList ->
            gameDao.insertAll(*gameList.map {
                GameEntity(
                    gameId = it.id ?: 0,
                    name = it.name ?: "",
                    backgroundImage = it.backgroundImage,
                    rating = it.rating,
                    releaseDate = it.releaseDate,
                    isLiked = false
                )
            }.toTypedArray())
        }
    }

    suspend fun getGameDetail(id: Long) {

        withContext(Dispatchers.IO) {
            when (val result = networkHelper.getGameDetails(id)) {
                is NetworkResult.Success -> {
                    onGameDetailSuccess(result.data)
                }
                is NetworkResult.Error -> {
                    channelGames.send(
                        RepoResponse.Error(ErrorModel(code = result.error.code, message = result.error.message))
                    )
                }
                is NetworkResult.Failure -> {
                    channelGames.send(
                        RepoResponse.Error(ErrorModel(exception = result.exception))
                    )
                }
            }

        }
    }

    private suspend fun onGameDetailSuccess(gamePojo: GamePojo) {
        val id = gamePojo.id?:return
        val gameEntity = gameDao.getGame(id)
        gameEntity.name = gamePojo.name ?: ""
        gameEntity.releaseDate = gamePojo.releaseDate
        gameEntity.backgroundImage = gamePojo.backgroundImage
        gameEntity.rating = gamePojo.rating
        gameEntity.releaseDate = gamePojo.releaseDate
        gameDao.update(gameEntity)
        val gameModel = GameModel(gameEntity)
        gameModel.description = gamePojo.description
        gameModel.metacriticRate = gamePojo.metacritic
        channelGames.send(
            RepoResponse.Data(GameListModel(games = mutableListOf(gameModel)))
        )
    }


    suspend fun toggleLike(gameId: Long) {
        withContext(Dispatchers.IO) {
            val entity = gameDao.getGame(gameId)
            entity.isLiked = !entity.isLiked
            gameDao.update(entity)
        }
    }

}