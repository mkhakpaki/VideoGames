package ir.mkhakpaki.videogames.ui.home

import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import ir.mkhakpaki.videogames.R
import ir.mkhakpaki.videogames.ui.model.GameItem
import ir.mkhakpaki.videogames.util.BaseAdapter

class GameHolder(itemView: View) :
    BaseAdapter.ChildViewHolder<GameItem>(itemView) {
    private lateinit var imageView: AppCompatImageView
    private lateinit var nameTv: AppCompatTextView
    private lateinit var detailsTv: AppCompatTextView

    init {
        setupHolder(this)
    }

    private fun setupHolder(holder: GameHolder) {
        setupView(holder.itemView)
    }

    private fun setupView(itemView: View) {
        imageView = itemView.findViewById(R.id.imageView)
        nameTv = itemView.findViewById(R.id.nameTv)
        detailsTv = itemView.findViewById(R.id.detailTv)
    }

    override fun fillInData(t: GameItem) {
        nameTv.text = t.game?.name
        val rating = t.game?.rating?.toString() ?: ""
        val released = t.game?.releaseDate ?: ""
        detailsTv.text = "$rating - $released"
        val circularProgressDrawable = CircularProgressDrawable(itemView.context)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.start()

        Glide
            .with(itemView.context)
            .load(t.game?.image)
            .centerCrop()
            .placeholder(circularProgressDrawable)
            .into(imageView)
    }

}