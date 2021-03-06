package ir.mkhakpaki.videogames.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ir.mkhakpaki.videogames.util.Constants

@Entity
data class GameEntity(
    @PrimaryKey val id:Long,
    @ColumnInfo(name = Constants.NAME) val name: String,
    @ColumnInfo(name = Constants.IMAGE_URL) val backgroundImage: String?,
    @ColumnInfo(name = Constants.RATING) val rating: Float?,
    @ColumnInfo(name = Constants.RELEASE_DATE) val releaseDate: String?,
    @ColumnInfo(name = Constants.IS_LIKED) val isLiked: Boolean
)