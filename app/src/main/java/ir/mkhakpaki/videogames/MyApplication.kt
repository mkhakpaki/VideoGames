package ir.mkhakpaki.videogames

import androidx.multidex.MultiDexApplication
import com.orhanobut.hawk.Hawk
import ir.mkhakpaki.videogames.di.AppComponent
import ir.mkhakpaki.videogames.di.AppComponentProvider
import ir.mkhakpaki.videogames.di.DaggerAppComponent

class MyApplication : MultiDexApplication(), AppComponentProvider {

    private lateinit var  appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        init()
    }

    private fun init() {
        appComponent = DaggerAppComponent.factory().create(this)
        appComponent.inject(this)

        Hawk.init(this).build()
    }

    override fun provideAppComponent(): AppComponent {
        return appComponent
    }

}