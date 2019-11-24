package io.moonshard.moonshard.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.moonshard.moonshard.R
import io.moonshard.moonshard.models.Category


interface CategoryMapListener {
    fun clickChat(categoryName: String)
}

class CategoryMapAdapter(val listener: CategoryMapListener, private var chats: List<Category>) :
    RecyclerView.Adapter<CategoryMapAdapter.ViewHolder>() {

    var focusedItem = -1


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.category_map_item,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.iconCategory?.setImageResource(chats[position].drawable)
        holder.categoryName?.text = chats[position].name
        holder.categoryInfo?.text = chats[position].name


        holder.itemView.setOnClickListener {
            focusedItem = position
            notifyDataSetChanged()
            listener.clickChat(chats[position].name)
        }
    }

    override fun getItemCount(): Int = chats.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        internal var categoryName: TextView? = view.findViewById(R.id.nameCategoryTv)
        internal var categoryInfo: TextView? = view.findViewById(R.id.categoryInfoTv)
        internal var iconCategory: ImageView? = view.findViewById(R.id.categoryIv)
        internal var mainLayout: LinearLayout? = view.findViewById(R.id.mainLayout)
    }
}