package ir.mkhakpaki.videogames.di.favorites

import dagger.Component
import ir.mkhakpaki.videogames.di.AppComponent
import ir.mkhakpaki.videogames.di.ViewModelFactoryModule
import ir.mkhakpaki.videogames.di.scope.PerFragment
import ir.mkhakpaki.videogames.ui.favorites.FavoritesFragment

@PerFragment
@Component(
    modules = [
        FavoritesBindsModule::class,
        ViewModelFactoryModule::class,
    ],
    dependencies = [AppComponent::class]
)
interface FavoritesComponent {
    fun inject(fragment: FavoritesFragment)
}