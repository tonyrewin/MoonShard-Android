package io.moonshard.moonshard.ui.adapters.profile.present

import android.R.attr.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import io.moonshard.moonshard.R
import io.moonshard.moonshard.common.utils.Utils


interface TypeTicketPresentListener {
    fun click()
}

class TypeTicketPresentAdapter(val listener: TypeTicketPresentListener, private var tickets: ArrayList<String>) :
    RecyclerView.Adapter<TypeTicketPresentAdapter.ViewHolder>()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.little_ticket_item,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(position==9){
            val params = holder.mainLayout?.layoutParams as ViewGroup.MarginLayoutParams
            params.marginStart = Utils.convertDpToPixel(8F, holder.mainLayout?.context)
            params.marginEnd = Utils.convertDpToPixel(8F, holder.mainLayout?.context)
            params.bottomMargin = Utils.convertDpToPixel(8F, holder.mainLayout?.context)
            params.topMargin = Utils.convertDpToPixel(8F, holder.mainLayout?.context)
        }

        holder.mainLayout?.setOnClickListener {
            listener.click()
        }
    }

    override fun getItemCount(): Int = 10

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        internal var titleTypeTv: TextView? = view.findViewById(R.id.titleType)
        internal var descriptionTicketTv: TextView? = view.findViewById(R.id.descriptionTicketTv)
        internal var mainLayout: CardView? = view.findViewById(R.id.mainView)
    }

}