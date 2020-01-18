package io.moonshard.moonshard.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.moonshard.moonshard.R
import io.moonshard.moonshard.models.api.Category
import io.moonshard.moonshard.ui.fragments.map.RoomsMap


interface CategoryMapListener {
    fun clickChat(category: Category)
}

class CategoryMapAdapter(val listener: CategoryMapListener, private var categories: ArrayList<Category>) :
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

        holder.iconCategory?.let {
            setDrawable(it,categories[position])
        }

        holder.categoryName?.text = categories[position].categoryName
        holder.categoryInfo?.text = categories[position].categoryName

        holder.itemView.setOnClickListener {
            RoomsMap.isFilter = true
            RoomsMap.category = categories[position]
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

    fun updateCategories(categories:ArrayList<Category>){
        this.categories.clear()
        this.categories.addAll(categories)
        notifyDataSetChanged()
    }


    override fun getItemCount(): Int = categories.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        internal var categoryName: TextView? = view.findViewById(R.id.nameCategoryTv)
        internal var categoryInfo: TextView? = view.findViewById(R.id.categoryInfoTv)
        internal var iconCategory: ImageView? = view.findViewById(R.id.categoryIv)
        internal var mainLayout: LinearLayout? = view.findViewById(R.id.mainLayout)
    }
}