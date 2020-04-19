package io.moonshard.moonshard.ui.adapters.tickets.statistic

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.moonshard.moonshard.R


interface StatisticTicketsListener {
    fun click()
}

class StatisticTicketsAdapter(val listener: StatisticTicketsListener, private var tickets: ArrayList<String>) :
    RecyclerView.Adapter<StatisticTicketsAdapter.ViewHolder>()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.statistic_ticket_item,
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
        internal var nameTicket: TextView? = view.findViewById(R.id.nameTicket)
        internal var valueTickets: TextView? = view.findViewById(R.id.valueTickets)
        internal var viewLine: View? = view.findViewById(R.id.viewLine)
    }

}