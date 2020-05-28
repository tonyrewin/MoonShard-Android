package io.moonshard.moonshard.ui.adapters.tickets

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.moonshardwallet.models.MyTicketSale
import io.moonshard.moonshard.R
import io.moonshard.moonshard.common.utils.Utils


interface TypesTicketListener {
    fun changeClick()
}

class TypesTicketAdapter (val listener: TypesTicketListener, private var ticketSales: ArrayList<MyTicketSale>) :
    RecyclerView.Adapter<TypesTicketAdapter.ViewHolder>()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.type_ticket_manage_item,
                parent,
                false
            )
        )

    fun update(ticketSales: ArrayList<MyTicketSale>) {
        this.ticketSales.clear()
        this.ticketSales.addAll(ticketSales)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.typeTicketTv?.text = "Название билета: " + ticketSales[position].typeTicket.toString()
        holder.priceTicketTv?.text = ticketSales[position].priceTicket.toString() + " ₽"
        holder.limitTv?.text = ticketSales[position].saleLimit.toString() + " билетов"

        if(position==ticketSales.size-1){
            val params = holder.mainLayout?.layoutParams as ViewGroup.MarginLayoutParams
            params.marginStart = Utils.convertDpToPixel(8F, holder.mainLayout?.context)
            params.marginEnd = Utils.convertDpToPixel(8F, holder.mainLayout?.context)
            params.bottomMargin = Utils.convertDpToPixel(8F, holder.mainLayout?.context)
            params.topMargin = Utils.convertDpToPixel(8F, holder.mainLayout?.context)
        }
    }

    override fun getItemCount(): Int = ticketSales.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        internal var typeTicketTv: TextView? = view.findViewById(R.id.titleType)
        internal var priceTicketTv: TextView? = view.findViewById(R.id.costTv)
        internal var limitTv: TextView? = view.findViewById(R.id.quantityTv)
        internal var mainLayout: CardView? = view.findViewById(R.id.mainLayout)
    }

}