package ir.mkhakpaki.videogames.ui.home

import android.content.Context
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import ir.mkhakpaki.videogames.R
import ir.mkhakpaki.videogames.ui.model.GameItem


class SliderAdapter (private val context: Context ) :
    PagerAdapter() {

    private val items: MutableList<GameItem> = mutableListOf()

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return items.size
    }

    fun submitItems(list: List<GameItem>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }
    override fun instantiateItem(view: ViewGroup, position: Int): Any {
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val imageLayout: View = inflater.inflate(R.layout.item_slider, view, false)

        val imageView: AppCompatImageView = imageLayout.findViewById(R.id.image) as AppCompatImageView
        Glide
            .with(context)
            .load(items[position].game?.image)
            .centerCrop()
            .placeholder(android.R.drawable.progress_indeterminate_horizontal)
            .into(imageView)
        view.addView(imageLayout)
        return imageLayout
    }


}