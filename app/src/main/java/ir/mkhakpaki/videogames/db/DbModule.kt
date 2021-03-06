package ir.mkhakpaki.videogames.db

import androidx.room.Room
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object DbModule {


    @Singleton
    @Provides
    fun providesRoomDatabase(): AppDatabase {
        return AppDatabase_Impl()
    }

    @Singleton
    @Provides
    fun providesGameDao(database: AppDatabase): GameDao {
        return database.gameDao()
    }


}