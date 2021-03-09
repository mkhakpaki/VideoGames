package ir.mkhakpaki.videogames.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import ir.mkhakpaki.videogames.util.Constants

@Entity(indices = [Index(value = [Constants.GAME_ID], unique = true)])
data class GameEntity(
    @PrimaryKey(autoGenerate = true) val id:Long = 0,
    @ColumnInfo(name = Constants.GAME_ID) val gameId:Long,
    @ColumnInfo(name = Constants.NAME) var name: String,
    @ColumnInfo(name = Constants.IMAGE_URL) var backgroundImage: String?,
    @ColumnInfo(name = Constants.RATING) var rating: Float?,
    @ColumnInfo(name = Constants.RELEASE_DATE) var releaseDate: String?,
    @ColumnInfo(name = Constants.IS_LIKED) var isLiked: Boolean
)