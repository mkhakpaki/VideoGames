package ir.mkhakpaki.videogames.repository

import com.orhanobut.hawk.Hawk
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

    suspend fun getAllGames() {
        getGamesFromDB(null, null)
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
                return@withContext
            }
            requestGameList(1)
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

    suspend fun requestGameList(page: Int?, likedGamIds: List<Long>? = null) {
        withContext(Dispatchers.IO) {
            when (val result = networkHelper.listGames(page)) {
                is NetworkResult.Success -> {
                    val gameLisPojo = result.data
                    storeGames(gameLisPojo, likedGamIds)
                    val nextPage = extractNextPage(gameLisPojo.nextPage)
                    Hawk.put(Constants.NEXT_PAGE_TO_REQUEST, nextPage)
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

    private fun storeGames(gameListPojo: GameListPojo, likedGamIds: List<Long>?) {
        val games = gameListPojo.gameList ?: return
        games.forEach { pojo ->
            val gameId = pojo.id ?: return@forEach
            gameDao.getGame(gameId)?.let { entity ->
                updateGame(entity, pojo)
            } ?: kotlin.run {
                val gameEntity = GameEntity(
                    gameId = pojo.id ?: 0,
                    name = pojo.name ?: "",
                    backgroundImage = pojo.backgroundImage,
                    rating = pojo.rating,
                    releaseDate = pojo.releaseDate,
                    isLiked = likedGamIds?.contains(pojo.id) ?: false
                )
                gameDao.insert(gameEntity)
            }
        }
    }

    private fun updateGame(entity: GameEntity, pojo: GamePojo) {
        pojo.name?.let {
            entity.name = it
        }
        pojo.backgroundImage?.let {
            entity.backgroundImage = it
        }
        entity.rating = pojo.rating
        entity.releaseDate = pojo.releaseDate
        gameDao.update(entity)
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
        val gameEntity = gameDao.getGame(id) ?: return
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
            val entity = gameDao.getGame(gameId) ?: return@withContext
            entity.isLiked = !entity.isLiked
            gameDao.update(entity)
        }
    }

    suspend fun searchGames(query: String) {
        withContext(Dispatchers.IO) {
            val games = gameDao.searchGames("%${query}%")
            channelGames.send(
                RepoResponse.Data(
                    GameListModel(
                        isSearchMode = true,
                        games = games.map { GameModel(it) }.toMutableList()
                    )
                )
            )
        }
    }

    suspend fun refresh() {
        withContext(Dispatchers.IO) {
            val likedGameIds = gameDao.getLikedGames().map { it.gameId }
            val storedNextPage: Int = Hawk.get(Constants.NEXT_PAGE_TO_REQUEST, 2)
            gameDao.clearGames()
            for (page: Int in 1 until storedNextPage) {
                requestGameList(page, likedGameIds)
            }
        }

    }

}