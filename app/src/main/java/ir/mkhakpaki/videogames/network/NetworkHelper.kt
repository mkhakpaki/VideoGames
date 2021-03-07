package ir.mkhakpaki.videogames.network

import ir.mkhakpaki.videogames.network.pojo.GameListPojo
import ir.mkhakpaki.videogames.network.pojo.GamePojo
import retrofit2.Response
import javax.inject.Inject

class NetworkHelper @Inject constructor(private val restApi: RestApi) {

    suspend fun listGames(page:Int?):
            NetworkResult<GameListPojo> = safeCallDeferred(restApi.listGames(page ?: 1))

    suspend fun getGameDetails(id: Long):
            NetworkResult<GamePojo> = safeCallDeferred(restApi.getGameDetails(id))
}