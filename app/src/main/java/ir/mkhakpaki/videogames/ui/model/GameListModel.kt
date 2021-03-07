package ir.mkhakpaki.videogames.ui.model

data class GameListModel(
    var games: MutableList<GameModel> = mutableListOf(),
    var page: Int = 0,
    val nextPage: Int? = null,
    val ended:Boolean? = null
)
