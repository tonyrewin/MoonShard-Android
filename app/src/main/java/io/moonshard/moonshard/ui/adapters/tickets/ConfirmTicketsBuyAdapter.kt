package io.moonshard.moonshard.ui.adapters.tickets

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.moonshardwallet.models.MyTicketSale
import io.moonshard.moonshard.R

interface ConfirmTicketsBuyListener {
    fun click()
}

class ConfirmTicketsBuyAdapter(
    val listener: ConfirmTicketsBuyListener,
    private var ticketsSale: ArrayList<MyTicketSale>
) :
    RecyclerView.Adapter<ConfirmTicketsBuyAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.confirm_ticket_buy_item,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.typeTicketTv?.text = ticketsSale[holder.adapterPosition].typeTicket.toString()
        holder.costTicketTv?.text = ticketsSale[holder.adapterPosition].priceTicket + " ₽"

        //todo hardcore
        if (position == ticketsSale.size-1) {
            holder.viewLine?.visibility = View.GONE
        }

    }


    fun update(ticketSales: ArrayList<MyTicketSale>) {
        this.ticketsSale.clear()
        this.ticketsSale.addAll(ticketSales)
        notifyDataSetChanged()
    }


    override fun getItemCount(): Int = ticketsSale.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        internal var typeTicketTv: TextView? = view.findViewById(R.id.typeTicketTv)
        internal var ticketDescriptionTv: TextView? = view.findViewById(R.id.descriptionTicketTv)
        internal var costTicketTv: TextView? = view.findViewById(R.id.costTicketTv)
        internal var viewLine: View? = view.findViewById(R.id.viewLine)
    }

}