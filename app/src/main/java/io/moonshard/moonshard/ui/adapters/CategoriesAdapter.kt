package io.moonshard.moonshard.ui.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.hdodenhof.circleimageview.CircleImageView
import io.moonshard.moonshard.R
import io.moonshard.moonshard.common.utils.setSafeOnClickListener
import io.moonshard.moonshard.db.ChooseChatRepository
import io.moonshard.moonshard.models.api.Category

interface CategoryListener {
    fun clickChat(categoryName: Category)
}

class CategoriesAdapter(val listener: CategoryListener, private var categories: ArrayList<Category>) :
    RecyclerView.Adapter<CategoriesAdapter.ViewHolder>() {

    var focusedItem = -1
    private fun setFocusedItem(){
        for(i in categories.indices){
            if(categories[i].categoryName== ChooseChatRepository.category?.categoryName){
                focusedItem = i
            }
        }
    }

    fun updateCategories(categories:ArrayList<Category>){
        this.categories.clear()
        this.categories.addAll(categories)
        setFocusedItem()
        notifyDataSetChanged()
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

        holder.iconCategory?.let {
            setDrawable(it,categories[position])
        }

        holder.categoryName?.text = categories[position].categoryName

        holder.itemView.setSafeOnClickListener {
            focusedItem = position
            notifyDataSetChanged()
            listener.clickChat(categories[position])
        }
    }

    private fun setDrawable(imageView:ImageView, category:Category){
        when {
            category.categoryName=="Тусовки" -> {
                imageView.setImageResource(R.drawable.ic_star_category)
            }
            category.categoryName=="Бизнес ивенты" -> {
                imageView.setImageResource(R.drawable.ic_case_category)

            }
            category.categoryName=="Кружок по интересам" -> {
                imageView.setImageResource(R.drawable.ic_heart_category)

            }
            category.categoryName=="Культурные мероприятия" -> {
                imageView.setImageResource(R.drawable.ic_culture_category)
            }
        }
    }

    override fun getItemCount(): Int = categories.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        internal var categoryName: TextView? = view.findViewById(R.id.name)
        internal var iconCategory: CircleImageView? = view.findViewById(R.id.iconCategory)
        internal var mainLayout: LinearLayout? = view.findViewById(R.id.mainLayout)
    }
}