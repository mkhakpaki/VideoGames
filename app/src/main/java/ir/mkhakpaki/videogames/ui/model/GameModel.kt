package ir.mkhakpaki.videogames.ui.model

import ir.mkhakpaki.videogames.db.GameEntity

data class GameModel(
    val id: String,
    val gameId: Long,
    val name: String? = null,
    val image: String? = null,
    val rating: Float? = null,
    val releaseDate: String? = null,
    var isLiked: Boolean? = false,
    var description: String? = null,
    var metacriticRate:Int? = null
) : ListModel(id) {

    constructor(gameEntity: GameEntity) : this(
        id = gameEntity.gameId.toString(),
        gameId = gameEntity.gameId,
        name = gameEntity.name,
        image = gameEntity.backgroundImage,
        rating = gameEntity.rating,
        releaseDate = gameEntity.releaseDate,
        isLiked = gameEntity.isLiked
    )
}