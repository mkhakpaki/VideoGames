package ir.mkhakpaki.videogames.db

import androidx.room.*
import ir.mkhakpaki.videogames.util.Constants

@Dao
interface GameDao {

    @Query("SELECT * FROM GameEntity ORDER BY id ASC")
    fun getAll(): List<GameEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg games: GameEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(game: GameEntity)

    @Query("SELECT * FROM GameEntity WHERE ${Constants.IS_LIKED} = 1")
    fun getLikedGames(): List<GameEntity>

    @Query("DELETE FROM GameEntity")
    fun clearGames()

    @Query("SELECT * FROM GameEntity WHERE ${Constants.GAME_ID} = :gameId")
    fun getGame(gameId:Long) : GameEntity?

    @Update(entity = GameEntity::class)
    fun update(obj: GameEntity)

    @Query("SELECT * FROM GameEntity WHERE ${Constants.NAME} LIKE :search")
    fun searchGames(search: String):  List<GameEntity>

}