package io.moonshard.moonshard.ui.adapters.tickets

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.moonshard.moonshard.R

interface ConfirmTicketsBuyListener {
    fun click()

}

class ConfirmTicketsBuyAdapter(val listener: ConfirmTicketsBuyListener, private var tickets: ArrayList<String>) :
    RecyclerView.Adapter<ConfirmTicketsBuyAdapter.ViewHolder>()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.confirm_ticket_buy_item,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        //todo hardcore
        if(position==9){
            holder.viewLine?.visibility = View.GONE
        }

    }

    override fun getItemCount(): Int = 10

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        internal var typeTicketTv: TextView? = view.findViewById(R.id.typeTicketTv)
        internal var ticketDescriptionTv: TextView? = view.findViewById(R.id.descriptionTicketTv)
        internal var costTicketTv: TextView? = view.findViewById(R.id.costTicketTv)
        internal var viewLine: View? = view.findViewById(R.id.viewLine)
    }

}