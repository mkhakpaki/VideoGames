package ir.mkhakpaki.videogames.network

import ir.mkhakpaki.videogames.ui.model.ErrorModel


sealed class NetworkResult<out R : Any> {
    data class Success<out R : Any>(val data: R) : NetworkResult<R>()
    data class Error(val error: ErrorModel) : NetworkResult<Nothing>()
    data class Failure(val exception: Exception) : NetworkResult<Nothing>()
}

val <T> T.exhaustive: T
    get() = this
