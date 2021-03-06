package ir.mkhakpaki.videogames.network

import ir.mkhakpaki.videogames.network.pojo.GameListPojo
import ir.mkhakpaki.videogames.network.pojo.GamePojo
import retrofit2.Response
import javax.inject.Inject

class NetworkHelper @Inject constructor(private val restApi: RestApi) {

    suspend fun listGames(page:Int?):
            Response<GameListPojo> = restApi.listGames(page ?: 1).await()

    suspend fun getGameDetails(id: Long):
            Response<GamePojo> = restApi.getGameDetails(id).await()
}