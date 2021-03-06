package ir.mkhakpaki.videogames.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import ir.mkhakpaki.videogames.R
import ir.mkhakpaki.videogames.di.DaggerHomeComponent
import ir.mkhakpaki.videogames.di.findAppComponent
import kotlinx.android.synthetic.main.fragment_home.*
import javax.inject.Inject

class HomeFragment : Fragment(R.layout.fragment_home) {


    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by viewModels<HomeViewModel> { viewModelFactory }
    private var sliderAdapter: SliderAdapter? = null

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
            if (it.size > 3) {
                sliderAdapter?.submitItems(it.subList(0, 3))
            }
        }
    }

    private fun setupView() {
        sliderAdapter = SliderAdapter(requireContext())
        sliderViewPager.adapter = sliderAdapter
        tabLayout.setupWithViewPager(sliderViewPager)
    }

    override fun onDestroyView() {
        sliderAdapter = null
        super.onDestroyView()
    }
}