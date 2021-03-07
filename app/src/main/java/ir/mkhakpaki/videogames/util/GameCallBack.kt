package ir.mkhakpaki.videogames.util

open class GameCallBack<T> {
    open fun itemClick(item: T) {}
    open fun itemLike(item: T) {}
    open fun onTryAgain() {}
}
