package io.moonshard.moonshard.ui.adapters.tickets

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.moonshardwallet.models.MyTicket
import io.moonshard.moonshard.R

interface TicketListener {
    fun clickPlus()
    fun clickMinus()
    fun click(originSaleAddress: String)
}

class TicketsAdapter(val listener: TicketListener, private var tickets: ArrayList<MyTicket>) :
    RecyclerView.Adapter<TicketsAdapter.ViewHolder>()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.ticket_buy_item,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.typeTicketTv?.text = "Название:" + tickets[position].typeTicket
        holder.costTicketTv?.text = tickets[position].priceTicket + " ₽"


        holder.plusBtn?.setOnClickListener {
            holder.counterTv?.text =  (holder.counterTv?.text.toString().toInt() + 1).toString()
            listener.clickPlus()
        }

        holder.minusBtn?.setOnClickListener {
            if(holder.counterTv?.text!="0"){
                holder.counterTv?.text =  (holder.counterTv?.text.toString().toInt() - 1).toString()
                listener.clickMinus()
            }
        }

        holder?.itemView?.setOnClickListener {
            listener.click(tickets[position].originSaleAddress)
        }

        if(position == tickets.size-1){
            holder.viewLine?.visibility = View.GONE
        }
    }

    fun update(tickets: ArrayList<MyTicket>) {
        this.tickets.clear()
        this.tickets.addAll(tickets)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = tickets.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        internal var typeTicketTv: TextView? = view.findViewById(R.id.typeTicketTv)
        internal var ticketDescriptionTv: TextView? = view.findViewById(R.id.ticketDescriptionTv)
        internal var costTicketTv: TextView? = view.findViewById(R.id.costTicketTv)
        internal var minusBtn: ImageView? = view.findViewById(R.id.minusBtn)
        internal var plusBtn: ImageView? = view.findViewById(R.id.plusBtn)
        internal var counterTv: TextView? = view.findViewById(R.id.counterTv)
        internal var viewLine: View? = view.findViewById(R.id.viewLine)
    }

}