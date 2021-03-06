package ir.mkhakpaki.videogames.repository

import ir.mkhakpaki.videogames.db.GameDao
import ir.mkhakpaki.videogames.network.NetworkHelper
import javax.inject.Inject

class GameRepository @Inject constructor(
        private val networkHelper: NetworkHelper,
        private val gameDao: GameDao
) {

}