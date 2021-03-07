package ir.mkhakpaki.videogames.ui.home

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ir.mkhakpaki.videogames.R
import ir.mkhakpaki.videogames.di.DaggerHomeComponent
import ir.mkhakpaki.videogames.di.findAppComponent
import ir.mkhakpaki.videogames.ui.model.GameItem
import ir.mkhakpaki.videogames.util.GameCallBack
import kotlinx.android.synthetic.main.fragment_home.*
import javax.inject.Inject

class HomeFragment : Fragment(R.layout.fragment_home) {


    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by viewModels<HomeViewModel> { viewModelFactory }
    private var sliderAdapter: SliderAdapter? = null
    private var recyclerAdapter: GamesRecyclerAdapter? = null
    private var layoutManager: LinearLayoutManager? = null

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
            Log.i("GAMES", "observe: ${it.size}")
            if (it.size > 3) {
                sliderAdapter?.submitItems(it.subList(0, 3))
                recyclerAdapter?.submitList(it.subList(3, it.size))
            } else {
                recyclerAdapter?.submitList(it)
            }
        }
    }

    private fun setupView() {
        sliderAdapter = SliderAdapter(requireContext())
        sliderViewPager.adapter = sliderAdapter
        tabLayout.setupWithViewPager(sliderViewPager)

        layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        recyclerAdapter = GamesRecyclerAdapter(viewModel.gamesDiff, object : GameCallBack<GameItem>() {

        })
        gamesRv.layoutManager = layoutManager
        gamesRv.adapter = recyclerAdapter
    }

    override fun onDestroyView() {
        sliderAdapter = null
        recyclerAdapter = null
        layoutManager = null
        super.onDestroyView()
    }
}