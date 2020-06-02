package io.moonshard.moonshard.ui.fragments.mychats.chat.info.tickets.statics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moonshardwallet.models.TicketSaleStatistic
import io.moonshard.moonshard.R
import io.moonshard.moonshard.common.utils.setSafeOnClickListener
import io.moonshard.moonshard.presentation.presenter.chat.info.tickets.statistics.SalesStatisticTicketPresenter
import io.moonshard.moonshard.presentation.view.chat.info.tickets.statistics.SalesStatisticTicketView
import io.moonshard.moonshard.ui.adapters.tickets.TicketsAdapter
import io.moonshard.moonshard.ui.adapters.tickets.statistic.BalanceTicketListener
import io.moonshard.moonshard.ui.adapters.tickets.statistic.BalanceTicketsAdapter
import io.moonshard.moonshard.ui.adapters.tickets.statistic.StatisticTicketsAdapter
import io.moonshard.moonshard.ui.adapters.tickets.statistic.StatisticTicketsListener
import kotlinx.android.synthetic.main.fragment_buy_tickets.*
import kotlinx.android.synthetic.main.fragment_sales_statistic_ticket.*
import kotlinx.android.synthetic.main.fragment_sales_statistic_ticket.backBtn
import kotlinx.android.synthetic.main.fragment_statistic_tickets.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import java.util.ArrayList


class SalesStatisticTicketFragment : MvpAppCompatFragment(),
    SalesStatisticTicketView {

    @InjectPresenter
    lateinit var presenter: SalesStatisticTicketPresenter

    var idChat = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sales_statistic_ticket, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        arguments?.let {
            idChat = it.getString("chatId")
        }

        initSalesTicketsAdapter()
        initBalanceTicketsAdapter()

        backBtn?.setSafeOnClickListener {
            parentFragmentManager.popBackStack()
        }

        presenter.getSalesStatistic(idChat)
        presenter.getAllSaleStatistic(idChat)
    }

    private fun initSalesTicketsAdapter() {
        salesRv?.layoutManager = LinearLayoutManager(context)
        salesRv?.adapter = StatisticTicketsAdapter(object : StatisticTicketsListener {
            override fun click() {

            }
        }, arrayListOf())
    }

    private fun initBalanceTicketsAdapter() {
        balanceTicketRv?.layoutManager = LinearLayoutManager(context)
        balanceTicketRv?.adapter = BalanceTicketsAdapter(object : BalanceTicketListener {
            override fun click() {

            }
        }, arrayListOf())
    }

    override fun setSalesTicketsData(saleStatistic: ArrayList<TicketSaleStatistic>){
        (salesRv?.adapter as? StatisticTicketsAdapter)?.update(saleStatistic)
    }

    override fun setBalanceTicketsData(saleStatistic: ArrayList<TicketSaleStatistic>){
        (balanceTicketRv?.adapter as? BalanceTicketsAdapter)?.update(saleStatistic)
    }

    override fun showSaleStatisticData(allSold: String, allSaleLimit: String) {
        saleTicketTv?.text = "$allSold из $allSaleLimit"
    }

    override fun showBalanceStatisticData(balance: String) {
        balanceAllTv?.text = "$balance"
    }
}
