package ir.mkhakpaki.videogames.network

import ir.mkhakpaki.videogames.network.pojo.GameListPojo
import ir.mkhakpaki.videogames.network.pojo.GamePojo
import ir.mkhakpaki.videogames.util.API_PATH
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RestApi {

    @GET(API_PATH.LIST_GAMES)
    fun listGames(@Query("page") page: Int): Deferred<Response<GameListPojo>>

    @GET(API_PATH.GAMED_DETAILS)
    fun getGameDetails(@Path("id") id: Long): Deferred<Response<GamePojo>>
}