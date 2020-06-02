package io.moonshard.moonshard.ui.fragments.mychats.chat.info.tickets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moonshardwallet.models.MyTicketSale
import io.moonshard.moonshard.R
import io.moonshard.moonshard.common.utils.setSafeOnClickListener
import io.moonshard.moonshard.presentation.presenter.chat.info.tickets.ConfirmBuyTicketsPresenter
import io.moonshard.moonshard.presentation.view.chat.info.tickets.ConfirmBuyTicketsView
import io.moonshard.moonshard.ui.adapters.tickets.ConfirmTicketsBuyAdapter
import io.moonshard.moonshard.ui.adapters.tickets.ConfirmTicketsBuyListener
import kotlinx.android.synthetic.main.fragment_confirm_buy_tickets.*
import kotlinx.android.synthetic.main.fragment_confirm_buy_tickets.backBtn
import kotlinx.android.synthetic.main.fragment_confirm_buy_tickets.costTv
import kotlinx.android.synthetic.main.fragment_confirm_buy_tickets.ticketsCounterTv
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter


class ConfirmBuyTicketsFragment : MvpAppCompatFragment(),
    ConfirmBuyTicketsView {

    @InjectPresenter
    lateinit var presenter: ConfirmBuyTicketsPresenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_confirm_buy_tickets, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()

        backBtn?.setSafeOnClickListener {
            parentFragmentManager.popBackStack()
        }

        nextBtn?.setSafeOnClickListener {
            presenter.buyTickets()
        }
        presenter.getConfirmTickets()
    }

    private fun initAdapter() {
        confirmTicketsRv?.layoutManager = LinearLayoutManager(context)
        confirmTicketsRv?.adapter = ConfirmTicketsBuyAdapter(object : ConfirmTicketsBuyListener {
            override fun click() {
            }
        }, arrayListOf())
    }

    override fun setTickets(ticketSales: ArrayList<MyTicketSale>) {
        (confirmTicketsRv?.adapter as? ConfirmTicketsBuyAdapter)?.update(ticketSales)
    }

  override fun showCost( value:String) {
        costTv.text = "$value ₽"
    }

    override fun showAmount(value:String){
        counterTicketsTv?.text = "$value билета"
    }

    override fun showToast(text: String) {
        Toast.makeText(context!!, text, Toast.LENGTH_SHORT).show()
    }

    override fun back(){
        parentFragmentManager.popBackStack()
        parentFragmentManager.popBackStack()
    }
}
