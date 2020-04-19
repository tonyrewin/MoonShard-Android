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
import kotlinx.android.synthetic.main.fragment_not_used_tickets.*


class NotUsedTicketsFragment : Fragment() {

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
            fragmentManager?.popBackStack()
        }

        initAdapter()
    }

    private fun initAdapter() {
        statisticRv?.layoutManager = LinearLayoutManager(context)
        statisticRv?.adapter = StatisticTicketsAdapter(object : StatisticTicketsListener {
            override fun click() {

            }
        }, arrayListOf())
    }
}
