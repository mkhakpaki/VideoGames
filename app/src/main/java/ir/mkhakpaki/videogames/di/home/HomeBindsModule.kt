package ir.mkhakpaki.videogames.di.home

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ir.mkhakpaki.videogames.di.ViewModelKey
import ir.mkhakpaki.videogames.ui.home.HomeViewModel

@Module
abstract class HomeBindsModule {

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    abstract fun bindViewModel(viewModel: HomeViewModel): ViewModel
}