package ir.mkhakpaki.videogames

import android.app.Application
import androidx.multidex.MultiDexApplication
import ir.mkhakpaki.videogames.di.AppComponent
import ir.mkhakpaki.videogames.di.DaggerAppComponent

class MyApplication : MultiDexApplication() {

    private lateinit var  appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        init()
    }

    private fun init() {
        appComponent = DaggerAppComponent.factory().create(this)
        appComponent.inject(this)
    }

}