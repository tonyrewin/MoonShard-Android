package io.moonshard.moonshard.ui.adapters.tickets

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.moonshardwallet.models.MyTicketSale
import io.moonshard.moonshard.R
import io.moonshard.moonshard.presentation.presenter.adapters.ConfirmTicketsBuyAdapterPresenter
import io.moonshard.moonshard.presentation.view.adapters.ConfirmTicketsBuyAdapterView
import io.moonshard.moonshard.ui.adapters.MvpBaseAdapter
import moxy.MvpDelegate
import moxy.presenter.InjectPresenter

interface ConfirmTicketsBuyListener {
    fun click()
}

class ConfirmTicketsBuyAdapter(
    parentDelegate: MvpDelegate<*>,
    val listener: ConfirmTicketsBuyListener,
    private var ticketsSale: ArrayList<MyTicketSale>,val idChat:String
) :
    MvpBaseAdapter<ConfirmTicketsBuyAdapter.ViewHolder>(parentDelegate, 0.toString()),
    ConfirmTicketsBuyAdapterView {

    @InjectPresenter
    lateinit var presenter: ConfirmTicketsBuyAdapterPresenter

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.confirm_ticket_buy_item,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        presenter.onBindViewHolder(holder, holder.adapterPosition, listener,idChat)
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
        internal var ticketDescriptionTv: TextView? = view.findViewById(R.id.descriptionTicketTv)
        internal var costTicketTv: TextView? = view.findViewById(R.id.costTicketTv)
        internal var viewLine: View? = view.findViewById(R.id.viewLine)
    }

}