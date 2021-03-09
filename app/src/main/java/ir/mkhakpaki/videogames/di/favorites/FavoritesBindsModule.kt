package ir.mkhakpaki.videogames.di.favorites

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ir.mkhakpaki.videogames.di.ViewModelKey
import ir.mkhakpaki.videogames.ui.favorites.FavoritesViewModel

@Module
abstract class FavoritesBindsModule {

    @Binds
    @IntoMap
    @ViewModelKey(FavoritesViewModel::class)
    abstract fun bindViewModel(viewModel: FavoritesViewModel): ViewModel
}