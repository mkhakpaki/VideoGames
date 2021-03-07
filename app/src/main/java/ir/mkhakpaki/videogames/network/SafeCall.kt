package ir.mkhakpaki.videogames.network

import ir.mkhakpaki.videogames.ui.model.ErrorModel
import kotlinx.coroutines.Deferred
import retrofit2.Response

suspend inline fun <R : Any> safeCallDeferred(
    deferred: Deferred<Response<R>>
) = safeCall {
    val result = deferred.await()
    val body = result.body()
    val code = result.code()


    if (result.isSuccessful && body != null) {
        return@safeCall NetworkResult.Success(body)
    }

    return@safeCall NetworkResult.Error(ErrorModel(code))
}

suspend fun <R : Any> safeCall(
    call: suspend () -> NetworkResult<R>
): NetworkResult<R> {
    return try {
        call()
    } catch (e: Exception) {
        NetworkResult.Failure(e)
    }
}
