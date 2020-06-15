package io.moonshard.moonshard.ui.adapters.tickets

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.moonshardwallet.models.MyTicketSale
import io.moonshard.moonshard.R
import io.moonshard.moonshard.presentation.presenter.adapters.BuyTicketsAdapterPresenter
import io.moonshard.moonshard.presentation.view.adapters.BuyTicketsAdapterView
import io.moonshard.moonshard.ui.adapters.MvpBaseAdapter
import moxy.MvpDelegate
import moxy.presenter.InjectPresenter

interface TicketListener {
    fun clickPlus(ticketSale: MyTicketSale)
    fun clickMinus(ticketSale: MyTicketSale)
    fun click(originSaleAddress: String)
}

class BuyTicketsAdapter(
    parentDelegate: MvpDelegate<*>,
    val listener: TicketListener,
    private var ticketSales: ArrayList<MyTicketSale>,
    val idChat: String
) :
    MvpBaseAdapter<BuyTicketsAdapter.ViewHolder>(parentDelegate, 0.toString()),
    BuyTicketsAdapterView {

    @InjectPresenter
    lateinit var presenter: BuyTicketsAdapterPresenter

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.ticket_buy_item,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: BuyTicketsAdapter.ViewHolder, position: Int) {
        Log.d("BuyTicketsFragment2",idChat)
        presenter.onBindViewHolder(holder, holder.adapterPosition, listener, idChat)
    }

    fun update(ticketSales: ArrayList<MyTicketSale>) {
        presenter.setData(ticketSales)
    }

    override fun onDataChange() {
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = presenter.getChatListSize()

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