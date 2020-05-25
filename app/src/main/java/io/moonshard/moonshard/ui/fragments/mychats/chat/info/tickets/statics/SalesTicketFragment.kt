package io.moonshard.moonshard.ui.fragments.mychats.chat.info.tickets.statics

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager

import io.moonshard.moonshard.R
import io.moonshard.moonshard.common.utils.setSafeOnClickListener
import io.moonshard.moonshard.ui.adapters.tickets.statistic.StatisticTicketsAdapter
import io.moonshard.moonshard.ui.adapters.tickets.statistic.StatisticTicketsListener
import kotlinx.android.synthetic.main.fragment_sales_ticket.*


class SalesTicketFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sales_ticket, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSalesTicketsAdapter()
        initBalanceTicketsAdapter()

        backBtn?.setSafeOnClickListener {
            fragmentManager?.popBackStack()
        }
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
        balanceTicketRv?.adapter = StatisticTicketsAdapter(object : StatisticTicketsListener {
            override fun click() {

            }
        }, arrayListOf())
    }
}
