package io.moonshard.moonshard.ui.adapters.tickets

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.moonshardwallet.models.MyTicketSale
import io.moonshard.moonshard.R
import io.moonshard.moonshard.presentation.presenter.adapters.TypesTicketAdapterPresenter
import io.moonshard.moonshard.presentation.view.adapters.TypesTicketAdapterView
import io.moonshard.moonshard.ui.adapters.MvpBaseAdapter
import moxy.MvpDelegate
import moxy.presenter.InjectPresenter


interface TypesTicketListener {
    fun changeClick()
}

class TypesTicketAdapter(
    parentDelegate: MvpDelegate<*>,
    val listener: TypesTicketListener,
    private var ticketSales: ArrayList<MyTicketSale>,
    val idChat: String
) :
    MvpBaseAdapter<TypesTicketAdapter.ViewHolder>(parentDelegate, 0.toString()),
    TypesTicketAdapterView {

    @InjectPresenter
    lateinit var presenter: TypesTicketAdapterPresenter

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.type_ticket_manage_item,
                parent,
                false
            )
        )

    fun update(ticketSales: ArrayList<MyTicketSale>) {
        presenter.setData(ticketSales)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        presenter.onBindViewHolder(holder, holder.adapterPosition, listener,idChat)
    }

    override fun onDataChange() {
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = presenter.getChatListSize()

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        internal var typeTicketTv: TextView? = view.findViewById(R.id.titleType)
        internal var priceTicketTv: TextView? = view.findViewById(R.id.costTv)
        internal var limitTv: TextView? = view.findViewById(R.id.quantityTv)
        internal var mainLayout: CardView? = view.findViewById(R.id.mainLayout)
    }

}