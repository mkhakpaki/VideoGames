package ir.mkhakpaki.videogames.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ir.mkhakpaki.videogames.R
import ir.mkhakpaki.videogames.di.findAppComponent
import ir.mkhakpaki.videogames.di.home.DaggerHomeComponent
import ir.mkhakpaki.videogames.ui.gameDetails.GameDetailsActivity
import ir.mkhakpaki.videogames.ui.model.GameItem
import ir.mkhakpaki.videogames.ui.model.GameModel
import ir.mkhakpaki.videogames.ui.model.ViewStateModel
import ir.mkhakpaki.videogames.util.Constants
import ir.mkhakpaki.videogames.util.GameCallBack
import ir.mkhakpaki.videogames.util.ListLoadMoreListener
import ir.mkhakpaki.videogames.util.OnLoadMoreListener
import kotlinx.android.synthetic.main.fragment_home.*
import javax.inject.Inject

class HomeFragment : Fragment(R.layout.fragment_home) {


    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by viewModels<HomeViewModel> { viewModelFactory }
    private var sliderAdapter: SliderAdapter? = null
    private var recyclerAdapter: GamesRecyclerAdapter? = null
    private var layoutManager: LinearLayoutManager? = null
    private var listLoadMoreListener: ListLoadMoreListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DaggerHomeComponent.builder()
            .appComponent(findAppComponent())
            .build()
            .inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        observe()
    }

    private fun observe() {
        viewModel.itemsLiveData.observe(viewLifecycleOwner) {
            listLoadMoreListener?.setLoaded(false)
            if (it.size > 3) {
                sliderAdapter?.submitItems(it.subList(0, 3))
                recyclerAdapter?.submitList(it.subList(3, it.size))
            } else {
                recyclerAdapter?.submitList(it)
            }
        }

        viewModel.searchItemsLiveData.observe(viewLifecycleOwner) {
            listLoadMoreListener?.ended = true
            listLoadMoreListener?.setLoaded(false)
            recyclerAdapter?.submitList(it)
        }

        viewModel.stateViewLiveData.observe(viewLifecycleOwner) {
            when (it) {

                ViewStateModel.LIST_END -> {
                    listLoadMoreListener?.ended = true
                }

                ViewStateModel.LOADING -> {
                    loadingState()
                }

                ViewStateModel.DATA -> {
                    dataState()
                }

                ViewStateModel.EMPTY -> {
                    emptyState()
                }

                ViewStateModel.SEARCH -> {
                    searchState()
                }
            }
        }

    }

    private fun emptyState() {
        pbLoading.visibility = View.GONE
        sliderViewPager.visibility = View.GONE
        tabLayout.visibility = View.GONE
        refreshLayout.visibility = View.GONE
        emptyTv.visibility = View.VISIBLE
    }

    private fun searchState() {
        pbLoading.visibility = View.VISIBLE
        sliderViewPager.visibility = View.GONE
        emptyTv.visibility = View.GONE
        pbLoading.visibility = View.GONE
        tabLayout.visibility = View.GONE
        refreshLayout.apply {
            visibility = View.VISIBLE
            isEnabled = false
            val params = layoutParams as ConstraintLayout.LayoutParams
            params.width = ConstraintLayout.LayoutParams.MATCH_PARENT
            params.height = 0
            params.topMargin = resources.getDimensionPixelSize(R.dimen.margin_8)
            params.leftMargin = resources.getDimensionPixelSize(R.dimen.margin_12)
            params.rightMargin = resources.getDimensionPixelSize(R.dimen.margin_12)
            params.startToStart = root.id
            params.endToEnd = root.id
            params.bottomToBottom = root.id
            params.topToBottom = searchView.id
            layoutParams = params
            requestLayout()
        }
    }

    private fun dataState() {
        pbLoading.visibility = View.GONE
        sliderViewPager.visibility = View.VISIBLE
        tabLayout.visibility = View.VISIBLE
        emptyTv.visibility = View.GONE
        refreshLayout.apply {
            visibility = View.VISIBLE
            isEnabled = true
            val params = layoutParams as ConstraintLayout.LayoutParams
            params.width = ConstraintLayout.LayoutParams.MATCH_PARENT
            params.height = 0
            params.topMargin = resources.getDimensionPixelSize(R.dimen.margin_8)
            params.leftMargin = resources.getDimensionPixelSize(R.dimen.margin_12)
            params.rightMargin = resources.getDimensionPixelSize(R.dimen.margin_12)
            params.startToStart = root.id
            params.endToEnd = root.id
            params.bottomToBottom = root.id
            params.topToBottom = tabLayout.id
            layoutParams = params
            requestLayout()
        }
    }

    private fun loadingState() {
        pbLoading.visibility = View.VISIBLE
        sliderViewPager.visibility = View.GONE
        tabLayout.visibility = View.GONE
        refreshLayout.visibility = View.GONE
        emptyTv.visibility = View.GONE
    }

    private fun setupView() {
        sliderAdapter = SliderAdapter(requireContext(), object : GameCallBack<GameItem>() {
            override fun itemClick(item: GameItem) {
                val model = item.game ?: return
                openItem(model)
            }
        })
        sliderViewPager.adapter = sliderAdapter
        tabLayout.setupWithViewPager(sliderViewPager)

        layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        layoutManager?.let {
            listLoadMoreListener = ListLoadMoreListener(it)
            listLoadMoreListener?.setOnLoadMoreListener(object : OnLoadMoreListener {
                override fun onLoadMore() {
                    viewModel.loadMore()
                }

            })
            listLoadMoreListener?.let {
                gamesRv.addOnScrollListener(it)
            }
        }
        recyclerAdapter =
            GamesRecyclerAdapter(viewModel.gamesDiff, object : GameCallBack<GameItem>() {
                override fun onTryAgain() {
                    viewModel.tryLoadMore()
                }

                override fun itemClick(item: GameItem) {
                    val model = item.game ?: return
                    openItem(model)
                }
            })
        gamesRv.layoutManager = layoutManager
        gamesRv.adapter = recyclerAdapter

        refreshLayout.setOnRefreshListener {
            refreshLayout.isRefreshing = false
            viewModel.refresh()
        }

        searchEt.doAfterTextChanged {
            it?.toString()?.let { query ->
                if (query.length >= 3) {
                    viewModel.searchGames(query)
                } else if (query.isEmpty()) {
                    viewModel.closeSearch()
                }
            } ?: kotlin.run {
                viewModel.closeSearch()
            }
        }
    }

    override fun onDestroyView() {
        sliderAdapter = null
        recyclerAdapter = null
        layoutManager = null
        listLoadMoreListener?.release()
        listLoadMoreListener = null
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