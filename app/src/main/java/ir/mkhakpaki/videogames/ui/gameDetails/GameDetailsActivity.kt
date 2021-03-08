package ir.mkhakpaki.videogames.ui.gameDetails

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import ir.mkhakpaki.videogames.R
import ir.mkhakpaki.videogames.di.findAppComponent
import ir.mkhakpaki.videogames.di.gameDetails.DaggerGameDetailsComponent
import ir.mkhakpaki.videogames.ui.model.ViewStateModel
import ir.mkhakpaki.videogames.util.Constants
import kotlinx.android.synthetic.main.activity_game_details.*
import javax.inject.Inject

class GameDetailsActivity : AppCompatActivity() {


    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by viewModels<GameDetailsViewModel> { viewModelFactory }

    private lateinit var circularProgressDrawable : CircularProgressDrawable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_details)

        DaggerGameDetailsComponent
            .builder()
            .appComponent(findAppComponent()).build()
            .inject(this)

        setupUi()
        setupData()
        observe()
    }

    private fun observe() {
        viewModel.itemLiveData.observe(this) {
            it.image?.let {
                loadImage(it)
            }
            setLikeButtonState(it.isLiked ?: false)
            nameTv.text = it.name
            dateTv.text = it.releaseDate
            rateTv.text = it.metacriticRate.toString()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                descriptionTv.text = Html.fromHtml(it.description, Html.FROM_HTML_MODE_COMPACT)
            } else {
                descriptionTv.text = Html.fromHtml(it.description)
            }
        }

        viewModel.stateViewLiveData.observe(this) {
            when (it) {
                ViewStateModel.ERROR -> errorState()
                ViewStateModel.DATA -> dataState()
                ViewStateModel.LOADING -> loadingState()
            }
        }
    }

    private fun dataState() {
        scrollView.visibility = View.VISIBLE
        pbLoading.visibility = View.GONE
        errorTv.visibility = View.GONE
    }

    private fun errorState() {
        pbLoading.visibility = View.GONE
        scrollView.visibility = View.GONE
        errorTv.visibility = View.VISIBLE
        errorTv.text = viewModel.possibleError?.message
    }

    private fun loadingState() {
        pbLoading.visibility = View.VISIBLE
        scrollView.visibility = View.GONE
        errorTv.visibility = View.GONE
    }

    private fun setupUi() {
        circularProgressDrawable = CircularProgressDrawable(this)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.start()

        likeIv.setOnClickListener {
            setLikeButtonState(viewModel.toggleLike())
        }
    }

    private fun setupData() {
        intent?.extras?.let { extras ->
            extras.getLong(Constants.GAME_ID).let {
                viewModel.setGameId(it)
            }
            extras.getString(Constants.IMAGE_URL)?.let {
                loadImage(it)
            }

            extras.getBoolean(Constants.IS_LIKED).let {
                setLikeButtonState(it)
            }

        }
    }

    private fun loadImage(url: String) {
        Glide
            .with(this)
            .load(url)
            .centerCrop()
            .placeholder(circularProgressDrawable)
            .into(image)
    }

    private fun setLikeButtonState(liked: Boolean) {
        Log.i("game", "setLikeButtonState: $liked")
        if (liked) {
            DrawableCompat.setTint(
                DrawableCompat.wrap(likeIv.drawable),
                ContextCompat.getColor(this, R.color.orange)
            )
        } else {
            DrawableCompat.setTint(
                DrawableCompat.wrap(likeIv.drawable),
                ContextCompat.getColor(this, R.color.white)
            )
        }
        likeIv.requestLayout()
    }
}