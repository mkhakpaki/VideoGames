package ir.mkhakpaki.videogames.di.home

import dagger.Component
import ir.mkhakpaki.videogames.di.AppComponent
import ir.mkhakpaki.videogames.di.ViewModelFactoryModule
import ir.mkhakpaki.videogames.di.scope.PerFragment
import ir.mkhakpaki.videogames.ui.home.HomeFragment

@PerFragment
@Component(
    modules = [
        HomeBindsModule::class,
        ViewModelFactoryModule::class,
    ],
    dependencies = [AppComponent::class]
)
interface HomeComponent {
    fun inject(fragment: HomeFragment)
}