package io.moonshard.moonshard.ui.fragments.mychats.chat.info.tickets.statics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moonshardwallet.models.TicketSaleStatistic

import io.moonshard.moonshard.R
import io.moonshard.moonshard.common.utils.setSafeOnClickListener
import io.moonshard.moonshard.presentation.presenter.chat.info.tickets.statistics.ScannedTicketPresenter
import io.moonshard.moonshard.presentation.view.chat.info.tickets.statistics.ScannedTicketView
import io.moonshard.moonshard.ui.adapters.tickets.statistic.StatisticTicketsAdapter
import io.moonshard.moonshard.ui.adapters.tickets.statistic.StatisticTicketsListener
import kotlinx.android.synthetic.main.fragment_scanned_ticket.*
import kotlinx.android.synthetic.main.fragment_scanned_ticket.backBtn
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import java.util.ArrayList


class ScannedTicketFragment : MvpAppCompatFragment(),
    ScannedTicketView {

    @InjectPresenter
    lateinit var presenter: ScannedTicketPresenter

    var idChat = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_scanned_ticket, container, false)
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

        presenter.getScannedStatistic(idChat)
        presenter.getAllScanned(idChat)
    }



    private fun initAdapter() {
        scannedTicketsRv?.layoutManager = LinearLayoutManager(context)
        scannedTicketsRv?.adapter = StatisticTicketsAdapter(object : StatisticTicketsListener {
            override fun click() {

            }
        }, arrayListOf())
    }

    override fun setScannedTicketsData(saleStatistic: ArrayList<TicketSaleStatistic>){
        (scannedTicketsRv?.adapter as? StatisticTicketsAdapter)?.update(saleStatistic)
    }

    override fun showAllScannedStatistic(allScanned: String, allSold: String) {
        scannedTv?.text = "$allScanned из $allSold "
    }
}
