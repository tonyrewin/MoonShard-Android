package io.moonshard.moonshard.ui.adapters.tickets.statistic

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.moonshardwallet.models.TicketSaleStatistic
import io.moonshard.moonshard.R


interface BalanceTicketListener {
    fun click()
}

class BalanceTicketsAdapter(val listener: BalanceTicketListener, private var tickets: ArrayList<TicketSaleStatistic>) :
    RecyclerView.Adapter<BalanceTicketsAdapter.ViewHolder>()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.statistic_ticket_item,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val balance = tickets[holder.adapterPosition].all.toInt() - tickets[holder.adapterPosition].amount.toInt()

        holder.nameTicket?.text  =  tickets[holder.adapterPosition].typeTicket
        holder.valueTickets?.text  = balance.toString()

        if(holder.adapterPosition==tickets.size-1){
            holder.viewLine?.visibility = View.GONE
        }
    }

    fun update(ticketSales: ArrayList<TicketSaleStatistic>) {
        this.tickets.clear()
        this.tickets.addAll(ticketSales)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = tickets.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        internal var nameTicket: TextView? = view.findViewById(R.id.nameTicket)
        internal var valueTickets: TextView? = view.findViewById(R.id.valueTickets)
        internal var viewLine: View? = view.findViewById(R.id.viewLine)
    }
}