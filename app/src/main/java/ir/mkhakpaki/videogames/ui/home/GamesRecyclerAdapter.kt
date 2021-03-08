package ir.mkhakpaki.videogames.ui.home

import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ir.mkhakpaki.videogames.R
import ir.mkhakpaki.videogames.ui.model.GameItem
import ir.mkhakpaki.videogames.util.Constants.TYPE_GAME_ITEM_LIST
import ir.mkhakpaki.videogames.util.BaseAdapter
import ir.mkhakpaki.videogames.util.GameCallBack

class GamesRecyclerAdapter (
    diffUtils: DiffUtil.ItemCallback<GameItem>,
    private val gameCallBack: GameCallBack<GameItem>
) : BaseAdapter<GameItem>(diffUtils, gameCallBack) {

    override fun onCreateChildViewHolder(
        parent: ViewGroup,
        viewType: Int
    ):
            RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_GAME_ITEM_LIST ->
                GameHolder(createView(parent, R.layout.item_game), gameCallBack )

            else -> null!!
        }
    }

    override fun getChildItemViewType(position: Int): Int {
        return when (val type = getItem(position).itemType) {
            TYPE_GAME_ITEM_LIST -> type
            else -> throw IllegalStateException("Incorrect type")
        }
    }

    override fun onBindChildViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(getItemViewType(position)) {
            TYPE_GAME_ITEM_LIST -> (holder as GameHolder).fillInData(getItem(position))
        }
    }
}
