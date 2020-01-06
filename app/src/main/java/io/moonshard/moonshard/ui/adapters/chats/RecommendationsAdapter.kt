package io.moonshard.moonshard.ui.adapters.chats

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.moonshard.moonshard.R


interface RecommendationsListener {
    fun recommendationsClick(categoryName: String)
}

class RecommendationsAdapter(val listener: RecommendationsListener,
                             private var recommendations: List<String>) :
    RecyclerView.Adapter<RecommendationsAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    }

    override fun getItemCount(): Int = 10


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.recommendation_item,
                parent,
                false
            )
        )

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        internal var groupNameTv: TextView? = view.findViewById(R.id.groupNameTv)
        internal var categoryTv: TextView? = view.findViewById(R.id.categoryTv)
        internal var valueMembersTv: TextView? = view.findViewById(R.id.valueMembersTv)
        internal var avatarEvent: ImageView? = view.findViewById(R.id.avatarEvent)
    }
}