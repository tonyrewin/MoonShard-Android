package io.moonshard.moonshard.ui.fragments.mychats.chat.info.tickets

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager

import io.moonshard.moonshard.R
import io.moonshard.moonshard.ui.adapters.TicketListener
import io.moonshard.moonshard.ui.adapters.TicketsAdapter
import kotlinx.android.synthetic.main.fragment_buy_tickets.*


class BuyTicketsFragment : Fragment() {

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


        backBtn?.setOnClickListener {
            fragmentManager?.popBackStack()
        }

        closeBtn?.setOnClickListener {
            fragmentManager?.popBackStack()
        }
    }

    private fun initAdapter() {
        ticketsRv?.layoutManager = LinearLayoutManager(context)
        ticketsRv?.adapter = TicketsAdapter(object : TicketListener {
            override fun clickPlus() {
            }

            override fun clickMinus() {
            }
        }, arrayListOf())
    }
}
