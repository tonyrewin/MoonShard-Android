package io.moonshard.moonshard.ui.fragments.mychats.chat.info.tickets.buyticket

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moonshardwallet.models.MyTicketSale

import io.moonshard.moonshard.R
import io.moonshard.moonshard.common.utils.setSafeOnClickListener
import io.moonshard.moonshard.presentation.presenter.chat.info.tickets.BuyTicketsPresenter
import io.moonshard.moonshard.presentation.view.chat.info.tickets.BuyTicketsView
import io.moonshard.moonshard.ui.adapters.tickets.TicketListener
import io.moonshard.moonshard.ui.adapters.tickets.TicketsAdapter
import io.moonshard.moonshard.ui.fragments.mychats.chat.MainChatFragment
import kotlinx.android.synthetic.main.fragment_buy_tickets.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter


class BuyTicketsFragment : MvpAppCompatFragment(),
    BuyTicketsView {

    @InjectPresenter
    lateinit var presenter: BuyTicketsPresenter

    var idChat = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_buy_tickets, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()

        arguments?.let {
            idChat = it.getString("chatId")
        }


        backBtn?.setSafeOnClickListener {
            parentFragmentManager.popBackStack()
        }

        closeBtn?.setSafeOnClickListener {
            fragmentManager?.popBackStack()
        }


        nextBtn?.setSafeOnClickListener {
            (parentFragment as? MainChatFragment)?.showConfirmBuyTicketsFragment(idChat,this)
        }

        presenter.getTypesTicket(idChat)
    }

    private fun initAdapter() {
        ticketsRv?.layoutManager = LinearLayoutManager(context)
        ticketsRv?.adapter =
            TicketsAdapter(object :
                TicketListener {

                override fun clickPlus(ticketSale: MyTicketSale) {
                    presenter.plusTicketSale(ticketSale)
                }

                override fun clickMinus(ticketSale: MyTicketSale) {
                    presenter.minusTicketSale(ticketSale)
                }

                override fun click(originSaleAddress: String) {
                    presenter.buyTicket(originSaleAddress,1)
                }
            }, arrayListOf())
    }

   override fun showCost(value:String){
       costTv?.text = "$value ₽"
    }

    override fun showAmount(value:String){
        ticketsCounterTv?.text = "$value билета"
    }

    override fun setTickets(ticketSales: ArrayList<MyTicketSale>) {
        (ticketsRv?.adapter as? TicketsAdapter)?.update(ticketSales)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        BuyTicketObject.ticketSales.clear()
    }
}
