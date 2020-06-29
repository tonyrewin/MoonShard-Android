package io.moonshard.moonshard.ui.fragments.mychats.chat.info.tickets.statics

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moonshardwallet.models.TicketSaleStatistic

import io.moonshard.moonshard.R
import io.moonshard.moonshard.common.utils.setSafeOnClickListener
import io.moonshard.moonshard.presentation.presenter.chat.info.tickets.statistics.NotUsedTicketsPresenter
import io.moonshard.moonshard.presentation.presenter.chat.info.tickets.statistics.ScannedTicketPresenter
import io.moonshard.moonshard.presentation.view.chat.info.tickets.statistics.NotUsedTicketsView
import io.moonshard.moonshard.presentation.view.chat.info.tickets.statistics.ScannedTicketView
import io.moonshard.moonshard.ui.adapters.tickets.statistic.StatisticTicketsAdapter
import io.moonshard.moonshard.ui.adapters.tickets.statistic.StatisticTicketsListener
import kotlinx.android.synthetic.main.fragment_not_used_tickets.*
import kotlinx.android.synthetic.main.fragment_not_used_tickets.backBtn
import kotlinx.android.synthetic.main.fragment_sales_statistic_ticket.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import java.util.ArrayList


class NotUsedTicketsFragment : MvpAppCompatFragment(),
    NotUsedTicketsView{

    @InjectPresenter
    lateinit var presenter: NotUsedTicketsPresenter

    var idChat = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_not_used_tickets, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            idChat = it.getString("chatId")
        }

        backBtn?.setSafeOnClickListener {
            parentFragmentManager.popBackStack()
        }

        initAdapter()

        presenter.getNotUsedStatistic(idChat)
        presenter.getNotUsedStatisticValue(idChat)
    }

    private fun initAdapter() {
        statisticRv?.layoutManager = LinearLayoutManager(context)
        statisticRv?.adapter = StatisticTicketsAdapter(object : StatisticTicketsListener {
            override fun click() {

            }
        }, arrayListOf())
    }

    override fun setNotUsedTicketsData(saleStatistic: ArrayList<TicketSaleStatistic>){
        (statisticRv?.adapter as? StatisticTicketsAdapter)?.update(saleStatistic)
    }


    override fun showAllNotUsedStatistic(notUsed: String, allSold: String) {
        notUsedTv?.text = "$notUsed из $allSold "
    }
}
