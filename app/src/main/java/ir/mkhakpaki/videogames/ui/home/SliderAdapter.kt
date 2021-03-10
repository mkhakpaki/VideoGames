package ir.mkhakpaki.videogames.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.mkhakpaki.videogames.R
import ir.mkhakpaki.videogames.ui.model.GameItem
import ir.mkhakpaki.videogames.util.GameCallBack


class SliderAdapter(
    private val context: Context,
    private val gameCallBack: GameCallBack<GameItem>
) :
    RecyclerView.Adapter<SlideHolder>() {

    private val items: MutableList<GameItem> = mutableListOf()

    fun submitItems(list: List<GameItem>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlideHolder {
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val imageLayout: View = inflater.inflate(R.layout.item_slide, parent, false)
        return SlideHolder(imageLayout, gameCallBack)
    }

    override fun onBindViewHolder(holder: SlideHolder, position: Int) {
        holder.fillInData(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }


}