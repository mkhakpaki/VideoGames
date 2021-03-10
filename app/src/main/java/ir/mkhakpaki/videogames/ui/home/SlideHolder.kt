package ir.mkhakpaki.videogames.ui.home

import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import ir.mkhakpaki.videogames.R
import ir.mkhakpaki.videogames.ui.model.GameItem
import ir.mkhakpaki.videogames.util.GameCallBack

class SlideHolder(view: View, private val callBack: GameCallBack<GameItem>) :
    RecyclerView.ViewHolder(view) {

    private lateinit var imageView: AppCompatImageView
    private lateinit var gameItem: GameItem

    init {
        setupHolder(this)
    }

    private fun setupHolder(holder: SlideHolder) {
        setupView(holder.itemView)
    }

    private fun setupView(itemView: View) {
        imageView = itemView.findViewById(R.id.image)
        imageView.setOnClickListener {
            callBack.itemClick(gameItem)
        }
    }

    fun fillInData(t: GameItem) {
        gameItem = t

        val circularProgressDrawable = CircularProgressDrawable(itemView.context)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.start()

        Glide
            .with(itemView.context)
            .load(gameItem.game?.image)
            .centerCrop()
            .placeholder(circularProgressDrawable)
            .into(imageView)
    }
}