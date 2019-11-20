package io.moonshard.moonshard.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.moonshard.moonshard.R
import io.moonshard.moonshard.models.Category

interface CategoryListener {
    fun clickChat(idChat: String)
}

class CategoriesAdapter(val listener: CategoryListener, private var chats: List<Category>) :
    RecyclerView.Adapter<CategoriesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.category_item,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: CategoriesAdapter.ViewHolder, position: Int) {

        holder.iconCategory?.setImageResource(chats[position].drawable)
        holder.categoryName?.text = chats[position].name

        holder.itemView.setOnClickListener {
            //listener.clickChat(chats[position].id)
        }
    }

    override fun getItemCount(): Int = chats.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        internal var categoryName: TextView? = view.findViewById(R.id.name)
        internal var iconCategory: ImageView? = view.findViewById(R.id.iconCategory)
    }
}