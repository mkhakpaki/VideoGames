package ir.mkhakpaki.videogames.di

import android.app.Application
import dagger.Module
import dagger.Provides
import ir.mkhakpaki.videogames.MyApplication
import javax.inject.Singleton

@Module
object AndroidModule {
    @Singleton
    @Provides
    @JvmStatic
    fun provideApplication(app: MyApplication): Application {
        return app
    }
}
