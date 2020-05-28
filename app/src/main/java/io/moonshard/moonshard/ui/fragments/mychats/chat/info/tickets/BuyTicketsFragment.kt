package io.moonshard.moonshard.ui.fragments.mychats.chat.info.tickets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moonshardwallet.MainService
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

        var tickets = MainService.getBuyTicketSErvice().myTicketsByOwner


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
            (parentFragment as? MainChatFragment)?.showConfirmBuyTicketsFragment(idChat)
        }

        presenter.getTypesTicket(idChat)


    }

    private fun initAdapter() {
        ticketsRv?.layoutManager = LinearLayoutManager(context)
        ticketsRv?.adapter =
            TicketsAdapter(object :
                TicketListener {
                override fun clickPlus() {
                }

                override fun clickMinus() {
                }

                override fun click(originSaleAddress: String) {
                    presenter.buyTicket(originSaleAddress,1)
                }
            }, arrayListOf())
    }

    override fun setTickets(ticketSales: ArrayList<MyTicketSale>) {
        (ticketsRv?.adapter as? TicketsAdapter)?.update(ticketSales)
    }
}
