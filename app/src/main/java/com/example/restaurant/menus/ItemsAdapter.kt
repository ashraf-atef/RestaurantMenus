package com.example.restaurant.menus

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurant.R
import com.example.restaurant.menus.data.items.Item
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_menu_item.view.*
import kotlinx.android.synthetic.main.item_tag.view.*
import javax.inject.Inject

//TODO: Make Base adapter [Low Priority]
class ItemsAdapter @Inject constructor(val itemClickListener: ItemClickListener) :
    RecyclerView.Adapter<ItemsAdapter.ItemViewHolder>() {

    private val list: MutableList<Item> = mutableListOf()

    override fun getItemCount(): Int = list.size

    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_menu_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(@NonNull holder: ItemViewHolder, position: Int) {
        holder.bind()
    }

    fun addData(items: List<Item>) {
        this.list.clear()
        this.list.addAll(items)
        notifyDataSetChanged()
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind() {
            with(list[adapterPosition]) {
                with(itemView) {
                Picasso.get().load(photoUrl).placeholder(R.drawable.ic_placeholder).into(iv_item_photo)
                    tv_item_name.text = name
                }
            }
        }
    }

    interface ItemClickListener {
        fun onItemMenuClick()
    }
}