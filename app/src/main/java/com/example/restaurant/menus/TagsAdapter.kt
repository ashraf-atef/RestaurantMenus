package com.example.restaurant.menus

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurant.R
import com.example.restaurant.common.presentationLayer.BaseDiffUtilCallback
import com.example.restaurant.menus.data.tags.Tag
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_retry.view.*
import kotlinx.android.synthetic.main.item_tag.view.*
import javax.inject.Inject


class TagsAdapter @Inject constructor(val itemClickListener: ItemClickListener) :
    RecyclerView.Adapter<TagsAdapter.BaseViewHolder>() {

    companion object {
        private const val DATA_LAYOUT = R.layout.item_tag
        private const val LOAD_MORE_LAYOUT = R.layout.item_loader
        private const val RETRY_LAYOUT = R.layout.item_retry
    }

    private val list: MutableList<Tag> = mutableListOf()
    private val diffUtilCallback: DataDiffCallback by lazy {
        DataDiffCallback()
    }
    var loadMore: Boolean = false
    var error: Boolean = false

    override fun getItemCount(): Int = if (loadMore || error) list.size + 1 else list.size

    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): BaseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return when (viewType) {
            DATA_LAYOUT -> DataViewHolder(view)
            LOAD_MORE_LAYOUT -> LoaderViewHolder(view)
            RETRY_LAYOUT -> RetryViewHolder(view)
            else -> throw IllegalArgumentException("Unknown Layout")
        }
    }

    override fun onBindViewHolder(@NonNull holder: BaseViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemViewType(position: Int): Int = when {
        loadMore && position == list.size -> LOAD_MORE_LAYOUT
        error && position == list.size -> RETRY_LAYOUT
        else -> DATA_LAYOUT
    }

    fun addTags(newTagList: List<Tag>) {
        removeLoadAndRetryRows()
        diffUtilCallback.setLists(list, newTagList)
        val diffResult = DiffUtil.calculateDiff(diffUtilCallback)
        diffResult.dispatchUpdatesTo(this)
        this.list.clear()
        this.list.addAll(newTagList)
    }

    private fun removeLoadAndRetryRows() {
        if (loadMore || error) {
            loadMore = false
            error = false
            notifyItemRemoved(list.size)
        }
    }

    fun addLoadMoreRow() {
        removeLoadAndRetryRows()
        loadMore = true
        notifyItemInserted(list.size)
    }

    fun addRetryRow() {
        removeLoadAndRetryRows()
        error = true
        notifyItemInserted(list.size)
    }

    abstract inner class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bind()
    }

    inner class DataViewHolder(itemView: View) : BaseViewHolder(itemView) {

        override fun bind() {
            with(list[adapterPosition]) {
                with (itemView) {
                Picasso.get().load(photoURL).placeholder(R.drawable.ic_placeholder).into(iv_tag)
                itemView.tv_tag_name.text = tagName
                itemView.setOnClickListener { itemClickListener.onTagClick(list[adapterPosition]) }
                }
            }
        }
    }

    inner class LoaderViewHolder(itemView: View) : BaseViewHolder(itemView) {
        override fun bind() {
        }
    }

    inner class RetryViewHolder(itemView: View) : BaseViewHolder(itemView) {

        override fun bind() {
            itemView.btn_retry_items.setOnClickListener { itemClickListener.onRetryTagsClick() }
        }
    }

    inner class DataDiffCallback : BaseDiffUtilCallback<Tag>() {

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldData[oldItemPosition].tagName == newData[newItemPosition].tagName

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldData[oldItemPosition] == newData[newItemPosition]
    }

    interface ItemClickListener {
        fun onTagClick(tag: Tag)
        fun onRetryTagsClick()
    }
}