package io.moonshard.moonshard.ui.adapters.profile.present

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.moonshardwallet.models.MyTicketSale
import com.example.moonshardwallet.models.Ticket
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.R
import io.moonshard.moonshard.common.utils.Utils
import io.moonshard.moonshard.presentation.presenter.adapters.TicketsAdapterPresenter
import io.moonshard.moonshard.presentation.view.adapters.TicketsAdapterView
import io.moonshard.moonshard.ui.adapters.MvpBaseAdapter
import moxy.MvpDelegate
import moxy.presenter.InjectPresenter


interface TicketPresentListener {
    fun click(ticket: Ticket)
    fun click(ticket: Ticket,ticketName:String)
}

class TicketPresentAdapter(parentDelegate: MvpDelegate<*>, val listener: TicketPresentListener, private var tickets: ArrayList<Ticket>) :
    MvpBaseAdapter<TicketPresentAdapter.ViewHolder>(parentDelegate,0.toString()) ,TicketsAdapterView {

    @InjectPresenter
    lateinit var presenter: TicketsAdapterPresenter

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.my_ticket_item,
                parent,
                false
            )
        )

    fun setData(tickets: ArrayList<Ticket>) {
        presenter.setData(tickets)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        presenter.onBindViewHolder(holder,holder.adapterPosition,listener)
    }

    override fun getItemCount(): Int = presenter.getChatListSize()

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        internal var avatarTicket:ImageView? = view.findViewById(R.id.avatarTicket)
        internal var titleTypeTv: TextView? = view.findViewById(R.id.labelTicket)
        internal var scannedIv:ImageView? = view.findViewById(R.id.scannedIv)
        internal var startDateTicket: TextView? = view.findViewById(R.id.startDateTicket)
        internal var locationTicket: TextView? = view.findViewById(R.id.locationTicket)
        internal var typeTicket: TextView? = view.findViewById(R.id.typeTicket)
        internal var placeTicket: TextView? = view.findViewById(R.id.placeTicket)
        internal var numberTicketTv: TextView? = view.findViewById(R.id.numberTicketTv)
        internal var mainLayout: CardView? = view.findViewById(R.id.mainView)
    }

    override fun onDataChange() {
        MainApplication.getMainUIThread().post {
            notifyDataSetChanged()
        }
    }

}