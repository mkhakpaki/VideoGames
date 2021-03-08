package ir.mkhakpaki.videogames.di.gameDetails

import dagger.Component
import ir.mkhakpaki.videogames.di.AppComponent
import ir.mkhakpaki.videogames.di.ViewModelFactoryModule
import ir.mkhakpaki.videogames.di.scope.PerActivity
import ir.mkhakpaki.videogames.ui.gameDetails.GameDetailsActivity

@PerActivity
@Component(
    modules = [
        GameDetailsBindsModule::class,
        ViewModelFactoryModule::class,
    ],
    dependencies = [AppComponent::class]
)
interface GameDetailsComponent {
    fun inject(activity: GameDetailsActivity)
}