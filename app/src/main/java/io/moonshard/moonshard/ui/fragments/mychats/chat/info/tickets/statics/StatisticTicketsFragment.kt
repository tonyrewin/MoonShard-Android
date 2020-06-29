package io.moonshard.moonshard.ui.fragments.mychats.chat.info.tickets.statics

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import io.moonshard.moonshard.R
import io.moonshard.moonshard.common.utils.setSafeOnClickListener
import io.moonshard.moonshard.presentation.presenter.chat.info.tickets.statistics.SalesStatisticTicketPresenter
import io.moonshard.moonshard.presentation.presenter.chat.info.tickets.statistics.StatisticTicketsPresenter
import io.moonshard.moonshard.presentation.view.chat.info.tickets.statistics.SalesStatisticTicketView
import io.moonshard.moonshard.presentation.view.chat.info.tickets.statistics.StatisticTicketsView
import io.moonshard.moonshard.ui.fragments.mychats.chat.MainChatFragment
import kotlinx.android.synthetic.main.fragment_statistic_tickets.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter


class StatisticTicketsFragment : MvpAppCompatFragment(),
    StatisticTicketsView {

    @InjectPresenter
    lateinit var presenter: StatisticTicketsPresenter


    var idChat = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_statistic_tickets, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            idChat = it.getString("chatId")
        }

        notUsedTicketsBtn?.setSafeOnClickListener {
            (parentFragment as? MainChatFragment)?.showNotUsedTicketsFragment(idChat)
        }

        searchLayout?.setSafeOnClickListener {
            (parentFragment as? MainChatFragment)?.showSearchTicketFragment(idChat)
        }

        backBtn?.setSafeOnClickListener {
            parentFragmentManager.popBackStack()
        }

        fillUpWalletBtn?.setSafeOnClickListener {
            (parentFragment as? MainChatFragment)?.showSalesTicketFragment(idChat)
        }

        scannedTicketsBtn?.setSafeOnClickListener {
            (parentFragment as? MainChatFragment)?.showScannedTicketFragment(idChat)
        }

        presenter.getSaleStatistic(idChat)

    }

    override fun showSaleStatisticData(allSold: String, allSaleLimit: String) {
        saleTicketView?.text = "$allSold из $allSaleLimit"
    }

    override fun showScannedStatisticData(allScanned: String, allSold: String) {
        scannedTv?.text = "$allScanned из $allSold "
    }

    override fun showNotUsedStatisticData(notUsed: String, allSold: String) {
        notUsedTv?.text = "$notUsed из $allSold "
    }

    override fun showProgressBar(){
        progressBar?.visibility = View.VISIBLE
    }

    override fun hideProgressBar(){
        progressBar?.visibility = View.GONE
    }
}
