package ir.mkhakpaki.videogames.repository

import android.util.Log
import ir.mkhakpaki.videogames.db.GameDao
import ir.mkhakpaki.videogames.db.GameEntity
import ir.mkhakpaki.videogames.network.NetworkHelper
import ir.mkhakpaki.videogames.network.pojo.GameListPojo
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
                            games = dbGames.map { mapDbGameToGameModel(it) }.toMutableList()
                        )
                    )
                )
            }
        }
    }

    suspend fun requestGameList(page: Int?) {
        withContext(Dispatchers.IO) {
            val result = networkHelper.listGames(page)

            if (result.code() == Constants.NETWORK_OK) {

                result.body()?.let {

                    storeGames(it, page ?: 1)
                    getGamesFromDB(it.nextPage == null, extractNextPage(it.nextPage))

                } ?: kotlin.run {

                    RepoResponse.Error(
                        ErrorModel(
                            code = Constants.NOT_FOUND,
                            message = result.message()
                        )
                    )

                }

            } else {
                channelGames.send(
                    RepoResponse.Error(ErrorModel(code = result.code(), message = result.message()))
                )
            }

        }
    }

    private fun extractNextPage(nextPage: String?): Int? {
        nextPage?.substringAfter("page=")?.let {
            return it.toInt()
        }
        return null
    }

    private fun mapDbGameToGameModel(gameEntity: GameEntity): GameModel {
        return GameModel(
            id = gameEntity.gameId.toString(),
            gameId = gameEntity.gameId,
            name = gameEntity.name,
            image = gameEntity.backgroundImage,
            rating = gameEntity.rating,
            releaseDate = gameEntity.releaseDate,
            isLiked = gameEntity.isLiked
        )
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

}