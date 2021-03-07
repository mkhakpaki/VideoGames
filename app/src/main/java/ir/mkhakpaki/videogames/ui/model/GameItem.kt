package ir.mkhakpaki.videogames.ui.model

import ir.mkhakpaki.videogames.util.Constants

data class GameItem(
    private val type: Int,
    val id: Long,
    val game: GameModel? = null
) : ItemClass(type) {

    override val itemType: Int
        get() = type

    companion object {


        fun makeLoadingItem() = GameItem(type = Constants.VIEW_TYPE_LOADING,
            Constants.VIEW_TYPE_LOADING.toLong())

        fun makeErrorItem() = GameItem(type = Constants.VIEW_TYPE_ERROR,
            Constants.VIEW_TYPE_ERROR.toLong())
    }
}
