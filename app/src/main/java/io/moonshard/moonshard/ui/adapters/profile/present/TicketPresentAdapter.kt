package io.moonshard.moonshard.ui.adapters.profile.present

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import io.moonshard.moonshard.R
import io.moonshard.moonshard.common.utils.Utils


interface TicketPresentListener {
    fun click()
}

class TicketPresentAdapter(val listener: TicketPresentListener, private var tickets: ArrayList<String>) :
    RecyclerView.Adapter<TicketPresentAdapter.ViewHolder>()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.my_ticket_item,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(position==9){
            val params = holder.mainLayout?.layoutParams as? ViewGroup.MarginLayoutParams
            params?.marginStart = Utils.convertDpToPixel(8F, holder.mainLayout?.context)
            params?.marginEnd = Utils.convertDpToPixel(8F, holder.mainLayout?.context)
            params?.bottomMargin = Utils.convertDpToPixel(8F, holder.mainLayout?.context)
            params?.topMargin = Utils.convertDpToPixel(8F, holder.mainLayout?.context)
        }

        holder.mainLayout?.setOnClickListener {
            listener.click()
        }
    }

    override fun getItemCount(): Int = 10

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        internal var titleTypeTv: TextView? = view.findViewById(R.id.labelTicket)
        internal var startDateTicket: TextView? = view.findViewById(R.id.startDateTicket)
        internal var locationTicket: TextView? = view.findViewById(R.id.locationTicket)
        internal var typeTicket: TextView? = view.findViewById(R.id.typeTicket)
        internal var placeTicket: TextView? = view.findViewById(R.id.placeTicket)
        internal var numberTicketTv: TextView? = view.findViewById(R.id.numberTicketTv)
        internal var mainLayout: CardView? = view.findViewById(R.id.mainView)
    }

}