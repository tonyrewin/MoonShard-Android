package io.moonshard.moonshard.ui.fragments.mychats.chat.info.tickets.buyticket

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moonshardwallet.models.MyTicketSale

import io.moonshard.moonshard.R
import io.moonshard.moonshard.common.utils.setSafeOnClickListener
import io.moonshard.moonshard.presentation.presenter.chat.info.tickets.BuyTicketsPresenter
import io.moonshard.moonshard.presentation.view.chat.info.tickets.BuyTicketsView
import io.moonshard.moonshard.ui.adapters.tickets.TicketListener
import io.moonshard.moonshard.ui.adapters.tickets.BuyTicketsAdapter
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

        showProgressBar()
        arguments?.let {
            idChat = it.getString("chatId")
            Log.d("BuyTicketsFragment",idChat)
            presenter.getRoomInfo(idChat)
        }

        initAdapter()

        backBtn?.setSafeOnClickListener {
            parentFragmentManager.popBackStack()
        }

        closeBtn?.setSafeOnClickListener {
            fragmentManager?.popBackStack()
        }

        nextBtn?.setSafeOnClickListener {
            (parentFragment as? MainChatFragment)?.showConfirmBuyTicketsFragment(idChat, this)
        }

        presenter.getTypesTicket(idChat)
    }

    private fun initAdapter() {
        Log.d("BuyTicketsFragment0",idChat)

        ticketsRv?.layoutManager = LinearLayoutManager(context)
        ticketsRv?.adapter =
            BuyTicketsAdapter(this.mvpDelegate,object :
                TicketListener {

                override fun clickPlus(ticketSale: MyTicketSale) {
                    presenter.plusTicketSale(ticketSale)
                }

                override fun clickMinus(ticketSale: MyTicketSale) {
                    presenter.minusTicketSale(ticketSale)
                }

                override fun click(originSaleAddress: String) {

                }
            }, arrayListOf(),idChat)
    }

    override fun showCost(value: String) {
        costTv?.text = "$value ₽"
    }

    override fun showAmount(value: String) {
        ticketsCounterTv?.text = "$value билета"
    }

    override fun setTickets(ticketSales: ArrayList<MyTicketSale>) {
        (ticketsRv?.adapter as? BuyTicketsAdapter)?.update(ticketSales)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        BuyTicketObject.ticketSales.clear()
    }

    override fun showProgressBar() {
        progressBar?.visibility = View.VISIBLE
    }

    override fun hideProgressBar() {
        progressBar?.visibility = View.GONE
    }

    override fun showNameEvent(name: String?) {
        labelTicket?.text = name
    }

    override fun showStartDateEvent(date: String?) {
        startDateTicket?.text = date
    }

    override fun showAvatarEvent(avatar: Bitmap) {
        avatarTicket?.setImageBitmap(avatar)
    }

    override fun showToast(text:String){
        Toast.makeText(activity, text, Toast.LENGTH_SHORT).show()
    }
}
