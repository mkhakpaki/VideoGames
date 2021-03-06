package ir.mkhakpaki.videogames.ui.model

data class GameModel(
    val id: String,
    val gameId: Long,
    val name:String? = null,
    val image:String? = null,
    val rating:Float? = null,
    val releaseDate:String? = null,
    val isLiked:Boolean? = false
) : ListModel(id)