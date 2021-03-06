package ir.mkhakpaki.videogames.di

import dagger.Component
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