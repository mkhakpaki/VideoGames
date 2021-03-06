package ir.mkhakpaki.videogames.db

import androidx.room.Room
import dagger.Module
import dagger.Provides
import ir.mkhakpaki.videogames.MyApplication
import javax.inject.Singleton

@Module
class DBModule {


    @Provides
    @Singleton
    fun provideDatabaseImpl(context: MyApplication): AppDatabase = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "video_games_db"
    ).build()


    @Provides
    fun providesGameDao(database: AppDatabase): GameDao = database.gameDao()


}