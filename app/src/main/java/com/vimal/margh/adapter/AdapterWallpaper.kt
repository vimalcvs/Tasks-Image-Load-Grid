package com.vimal.margh.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vimal.margh.R
import com.vimal.margh.databinding.ItemListBinding
import com.vimal.margh.databinding.ItemLoadMoreBinding
import com.vimal.margh.imageloader.ImageLoader
import com.vimal.margh.models.ModelWallpaper
import com.vimal.margh.util.Constant
import com.vimal.margh.util.Utils


class AdapterWallpaper(
    private val context: Context,
    view: RecyclerView,
    private val modelLists: MutableList<ModelWallpaper?>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val items = 1
    var scrolling: Boolean = false
    private var loading = false
    private var onLoadMoreListener: OnLoadMoreListener? = null
    private var mOnItemClickListener: OnItemClickListener? = null

    init {
        lastItemViewDetector(view)

        view.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    scrolling = true
                } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    scrolling = false
                }
                super.onScrollStateChanged(recyclerView, newState)
            }
        })
    }


    fun setOnItemClickListener(mItemClickListener: OnItemClickListener?) {
        this.mOnItemClickListener = mItemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == items) {
            OriginalViewHolder(
                ItemListBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ), parent, false
                )
            )
        } else {
            ProgressViewHolder(
                ItemLoadMoreBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ), parent, false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is OriginalViewHolder) {
            val modelList = modelLists[position]

            try {
                ImageLoader.with(context)
                    .load(modelList!!.previewURL)
                    .thumbnail(R.drawable.ic_placeholder)
                    .into(holder.binding.ivImage)

            } catch (e: Exception) {
                Utils.getErrors(e)
            }
            holder.binding.cvCard.setOnClickListener {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener!!.onItemClick(modelList)
                }
            }

        } else {
            (holder as ProgressViewHolder).binding.progressBar1.isIndeterminate = true
        }
    }

    override fun getItemCount(): Int {
        return modelLists.size
    }

    override fun getItemViewType(position: Int): Int {
        val modelList = modelLists[position]
        if (modelList != null) {
            modelList.previewURL
            if (modelList.previewURL.isEmpty()) {
                return 2
            }
            return items
        } else {
            return 0
        }
    }

    fun insertData(items: List<ModelWallpaper?>) {
        setLoaded()
        val positionStart = itemCount
        val itemCount = items.size
        modelLists.addAll(items)
        notifyItemRangeInserted(positionStart, itemCount)
    }

    fun setLoaded() {
        loading = false
        for (i in 0 until itemCount) {
            if (modelLists[i] == null) {
                modelLists.removeAt(i)
                notifyItemRemoved(i)
            }
        }
    }

    fun setLoading() {
        if (itemCount != 0) {
            modelLists.add(null)
            notifyItemInserted(itemCount - 1)
            loading = true
        }
    }

    fun setOnLoadMoreListener(onLoadMoreListener: OnLoadMoreListener?) {
        this.onLoadMoreListener = onLoadMoreListener
    }

    private fun lastItemViewDetector(recyclerView: RecyclerView) {
        if (recyclerView.layoutManager is LinearLayoutManager) {
            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastPos: Int = layoutManager.findLastVisibleItemPosition()
                    if (!loading && lastPos == itemCount - 1 && onLoadMoreListener != null) {
                        val currentPage = itemCount / (Constant.LOAD_MORE)
                        onLoadMoreListener!!.onLoadMore(currentPage)
                        loading = true
                    }
                }
            })
        }
    }

    interface OnLoadMoreListener {
        fun onLoadMore(int: Int)
    }

    interface OnItemClickListener {
        fun onItemClick(modelWallpaper: ModelWallpaper?)

        fun onItemDelete(modelWallpaper: ModelWallpaper?)
    }

    class OriginalViewHolder(val binding: ItemListBinding) : RecyclerView.ViewHolder(
        binding.root
    )

    class ProgressViewHolder(val binding: ItemLoadMoreBinding) : RecyclerView.ViewHolder(
        binding.root
    )
}