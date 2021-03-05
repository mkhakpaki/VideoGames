package ir.mkhakpaki.videogames.db

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ir.mkhakpaki.videogames.util.Constants

interface GameDao {

    @Query("SELECT * FROM GameEntity")
    fun getAll(): List<GameEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg games: GameEntity)

    @Query("SELECT * FROM GameEntity WHERE ${Constants.IS_LIKED} = 1")
    fun getLikedGames(): List<GameEntity>
}