package io.moonshard.moonshard.ui.fragments.mychats.chat.info.tickets

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager

import io.moonshard.moonshard.R
import io.moonshard.moonshard.common.utils.setSafeOnClickListener
import io.moonshard.moonshard.ui.adapters.tickets.ConfirmTicketsBuyAdapter
import io.moonshard.moonshard.ui.adapters.tickets.ConfirmTicketsBuyListener
import kotlinx.android.synthetic.main.fragment_confirm_buy_tickets.*


class ConfirmBuyTicketsFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_confirm_buy_tickets, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()

        backBtn?.setSafeOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun initAdapter() {
        confirmTicketsRv?.layoutManager = LinearLayoutManager(context)
        confirmTicketsRv?.adapter = ConfirmTicketsBuyAdapter(object : ConfirmTicketsBuyListener {
            override fun click() {
            }
        }, arrayListOf())
    }
}
