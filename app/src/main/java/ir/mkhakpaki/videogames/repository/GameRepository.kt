package ir.mkhakpaki.videogames.repository

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

    private suspend fun getGamesFromDB(ended: Boolean?, nextPage: Int?) {
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


    suspend fun getLikedGames() {
        withContext(Dispatchers.IO) {
            val dbGames = gameDao.getLikedGames()
            channelGames.send(
                RepoResponse.Data(
                    GameListModel(
                        games = dbGames.map { GameModel(it) }.toMutableList()
                    )
                )
            )
        }
    }

    suspend fun requestGameList(page: Int?) {
        withContext(Dispatchers.IO) {
            when (val result = networkHelper.listGames(page)) {
                is NetworkResult.Success -> {
                    val gameLisPojo = result.data
                    storeGames(gameLisPojo)
                    getGamesFromDB(
                        gameLisPojo.nextPage == null,
                        extractNextPage(gameLisPojo.nextPage)
                    )
                }
                is NetworkResult.Error -> {
                    channelGames.send(
                        RepoResponse.Error(
                            ErrorModel(
                                code = result.error.code,
                                message = result.error.message
                            )
                        )
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

    private fun storeGames(gameListPojo: GameListPojo) {
        gameListPojo.gameList?.let { gameList ->
            val oldDbGames = gameDao.getAll().toMutableList()
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

            oldDbGames.forEach { gameEntity ->
                val gamePojo =
                    gameListPojo.gameList?.find { it.id == gameEntity.gameId } ?: return@forEach
                gameEntity.name = gamePojo.name ?: ""
                gameEntity.releaseDate = gamePojo.releaseDate
                gameEntity.backgroundImage = gamePojo.backgroundImage
                gameEntity.rating = gamePojo.rating
                gameEntity.releaseDate = gamePojo.releaseDate
                gameDao.update(gameEntity)
            }
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
                        RepoResponse.Error(
                            ErrorModel(
                                code = result.error.code,
                                message = result.error.message
                            )
                        )
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
        val id = gamePojo.id ?: return
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
            entity.isLiked = !(entity.isLiked ?: false)
            gameDao.update(entity)
        }
    }

}