package ir.mkhakpaki.videogames.di.gameDetails

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ir.mkhakpaki.videogames.di.ViewModelKey
import ir.mkhakpaki.videogames.ui.gameDetails.GameDetailsViewModel

@Module
abstract class GameDetailsBindsModule {

    @Binds
    @IntoMap
    @ViewModelKey(GameDetailsViewModel::class)
    abstract fun bindViewModel(viewModel: GameDetailsViewModel): ViewModel
}