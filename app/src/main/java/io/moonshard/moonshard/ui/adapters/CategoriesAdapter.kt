package io.moonshard.moonshard.ui.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.moonshard.moonshard.R
import io.moonshard.moonshard.db.ChooseChatRepository
import io.moonshard.moonshard.models.Category

interface CategoryListener {
    fun clickChat(categoryName: String)
}

class CategoriesAdapter(val listener: CategoryListener, private var chats: List<Category>) :
    RecyclerView.Adapter<CategoriesAdapter.ViewHolder>() {

    var focusedItem = -1
    fun setFocusedItem(){
        for(i in chats.indices){
            if(chats[i].name== ChooseChatRepository.category){
                focusedItem = i
            }
        }
    }

    init {
        setFocusedItem()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.category_item,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (focusedItem == position) {
            holder.categoryName?.setTextColor(Color.parseColor("#0075FF"))
            holder.mainLayout?.setBackgroundColor(Color.parseColor("#EEF6FF"))
        }else{
            holder.mainLayout?.setBackgroundColor(Color.parseColor("#FFFFFF"))
            holder.categoryName?.setTextColor(Color.parseColor("#333333"))
        }

        holder.iconCategory?.setImageResource(chats[position].drawable)
        holder.categoryName?.text = chats[position].name

        holder.itemView.setOnClickListener {
            focusedItem = position
            notifyDataSetChanged()
            listener.clickChat(chats[position].name)
        }
    }

    override fun getItemCount(): Int = chats.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        internal var categoryName: TextView? = view.findViewById(R.id.name)
        internal var iconCategory: ImageView? = view.findViewById(R.id.iconCategory)
        internal var mainLayout: LinearLayout? = view.findViewById(R.id.mainLayout)
    }
}