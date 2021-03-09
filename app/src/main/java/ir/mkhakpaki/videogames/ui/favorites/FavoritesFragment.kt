package ir.mkhakpaki.videogames.ui.favorites

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ir.mkhakpaki.videogames.R
import ir.mkhakpaki.videogames.di.favorites.DaggerFavoritesComponent
import ir.mkhakpaki.videogames.di.findAppComponent
import ir.mkhakpaki.videogames.ui.gameDetails.GameDetailsActivity
import ir.mkhakpaki.videogames.ui.home.GamesRecyclerAdapter
import ir.mkhakpaki.videogames.ui.model.GameItem
import ir.mkhakpaki.videogames.ui.model.GameModel
import ir.mkhakpaki.videogames.ui.model.ViewStateModel
import ir.mkhakpaki.videogames.util.Constants
import ir.mkhakpaki.videogames.util.GameCallBack
import kotlinx.android.synthetic.main.fragment_favorites.*
import kotlinx.android.synthetic.main.fragment_home.gamesRv
import kotlinx.android.synthetic.main.fragment_home.pbLoading
import javax.inject.Inject

class FavoritesFragment : Fragment(R.layout.fragment_favorites) {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by viewModels<FavoritesViewModel> { viewModelFactory }
    private var recyclerAdapter: GamesRecyclerAdapter? = null
    private var layoutManager: LinearLayoutManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DaggerFavoritesComponent
            .builder()
            .appComponent(findAppComponent())
            .build()
            .inject(this)
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadLikedGames()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        observe()
    }

    private fun observe() {
        viewModel.itemsLiveData.observe(viewLifecycleOwner) {
            recyclerAdapter?.submitList(it)
        }

        viewModel.stateViewLiveData.observe(viewLifecycleOwner) {
            when (it) {
                ViewStateModel.LOADING -> {
                    loadingState()
                }
                ViewStateModel.DATA -> {
                    dataState()
                }
                ViewStateModel.EMPTY -> {
                    emptyState()
                }
                ViewStateModel.ERROR -> {
                    Toast.makeText(
                        requireContext(),
                        viewModel.possibleError?.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    }

    private fun emptyState() {
        pbLoading.visibility = View.GONE
        gamesRv.visibility = View.VISIBLE
        emptyTv.visibility = View.VISIBLE
    }

    private fun dataState() {
        pbLoading.visibility = View.GONE
        gamesRv.visibility = View.VISIBLE
        emptyTv.visibility = View.GONE
    }

    private fun loadingState() {
        pbLoading.visibility = View.VISIBLE
        gamesRv.visibility = View.GONE
        emptyTv.visibility = View.GONE
    }

    private fun setupView() {
        layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        recyclerAdapter =
            GamesRecyclerAdapter(viewModel.gamesDiff, object : GameCallBack<GameItem>() {

                override fun itemClick(item: GameItem) {
                    val model = item.game ?: return
                    openItem(model)
                }
            })
        gamesRv.layoutManager = layoutManager
        gamesRv.adapter = recyclerAdapter
    }

    override fun onDestroyView() {
        recyclerAdapter = null
        layoutManager = null
        super.onDestroyView()
    }

    private fun openItem(gameModel: GameModel) {
        Intent(requireContext(), GameDetailsActivity::class.java).apply {
            putExtra(Constants.GAME_ID, gameModel.gameId)
            putExtra(Constants.IMAGE_URL, gameModel.image)
            putExtra(Constants.IS_LIKED, gameModel.isLiked)
        }.also {
            startActivity(it)
        }
    }

}