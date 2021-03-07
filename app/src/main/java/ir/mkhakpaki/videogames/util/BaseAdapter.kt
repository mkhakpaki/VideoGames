package ir.mkhakpaki.videogames.util

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ir.mkhakpaki.videogames.R
import ir.mkhakpaki.videogames.ui.model.ItemClass
import ir.mkhakpaki.videogames.util.Constants.VIEW_TYPE_ERROR
import ir.mkhakpaki.videogames.util.Constants.VIEW_TYPE_LOADING


abstract class BaseAdapter<T : ItemClass>(
    diffCallback: DiffUtil.ItemCallback<T>,
    private val callback: GameCallBack<T>
) : ListAdapter<T, RecyclerView.ViewHolder>(diffCallback) {

    companion object {
        private const val MIN_LEGIT_TYPE = 3
    }

    protected var inflater: LayoutInflater? = null

    abstract class ChildViewHolder<T>(view: View) : RecyclerView.ViewHolder(view) {
        abstract fun fillInData(t: T)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        createInflater(parent.context)
        return when (viewType) {
            VIEW_TYPE_LOADING -> {
                LoadingViewHolder(createView(parent, R.layout.item_loading))
            }

            VIEW_TYPE_ERROR -> {
                ErrorViewHolder(createView(parent, R.layout.item_error), callback)
            }

            else -> onCreateChildViewHolder(parent, viewType)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (val type = currentList[position].itemType) {
            VIEW_TYPE_LOADING,
            VIEW_TYPE_ERROR -> type
            else -> {
                val childType = getChildItemViewType(position)
                if (childType < MIN_LEGIT_TYPE) {
                    throw IllegalStateException("You must use a number of 10 or greater for the type")
                }
                childType
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val type = getItemViewType(position)
        if (type == VIEW_TYPE_LOADING || type == VIEW_TYPE_ERROR) {
            return
        }
        onBindChildViewHolder(holder, position)
    }

    private fun createInflater(context: Context) {
        if (inflater == null) {
            inflater = LayoutInflater.from(context)
        }
    }

    fun createView(parent: ViewGroup, layoutId: Int): View {
        return inflater?.inflate(layoutId, parent, false) ?: View(parent.context)
    }

    abstract fun onCreateChildViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder

    abstract fun getChildItemViewType(position: Int): Int

    abstract fun onBindChildViewHolder(holder: RecyclerView.ViewHolder, position: Int)

    class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    class ErrorViewHolder(
        itemView: View,
        private var callback: GameCallBack<*>?
    ) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private val ivTryAgain: AppCompatImageView = itemView.findViewById(R.id.ivTryAgain)

        init {
            ivTryAgain.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            callback?.onTryAgain()
        }
    }
}
