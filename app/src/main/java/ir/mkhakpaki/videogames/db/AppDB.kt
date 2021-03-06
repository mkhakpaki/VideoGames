package ir.mkhakpaki.videogames.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [GameEntity::class],
    version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun gameDao(): GameDao
}