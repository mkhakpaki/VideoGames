package ir.mkhakpaki.videogames.di

import com.google.gson.Gson
import dagger.BindsInstance
import dagger.Component
import ir.mkhakpaki.videogames.MyApplication
import ir.mkhakpaki.videogames.db.AppDatabase
import ir.mkhakpaki.videogames.db.DBModule
import ir.mkhakpaki.videogames.db.GameDao
import ir.mkhakpaki.videogames.network.NetworkHelper
import ir.mkhakpaki.videogames.network.RestApi
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AndroidModule::class,
    NetworkModule::class,
    DBModule::class
])
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance app: MyApplication
        ): AppComponent
    }

    fun inject(app: MyApplication)

    fun getDB(): AppDatabase
    fun getNetWorkHelper(): NetworkHelper
    fun restApi(): RestApi
    fun getGson(): Gson
    fun getGameDao(): GameDao
}
