package io.moonshard.moonshard.ui.fragments.profile.mytickets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moonshardwallet.models.Ticket

import io.moonshard.moonshard.R
import io.moonshard.moonshard.presentation.presenter.profile.mytickets.MyTicketsPresenter
import io.moonshard.moonshard.presentation.view.profile.my_tickets.MyTicketsView
import io.moonshard.moonshard.ui.activities.MainActivity
import io.moonshard.moonshard.ui.adapters.profile.present.TicketPresentAdapter
import io.moonshard.moonshard.ui.adapters.profile.present.TicketPresentListener
import kotlinx.android.synthetic.main.fragment_my_tickets.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter


class MyTicketsFragment : MvpAppCompatFragment(), MyTicketsView {

    @InjectPresenter
    lateinit var presenter: MyTicketsPresenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_tickets, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).hideBottomNavigationBar()

        backBtn?.setOnClickListener {
            fragmentManager?.popBackStack()
        }
        initAdapter()

        presenter.getMyTickets()
    }

    private fun initAdapter() {
        myTicketsRv?.layoutManager = LinearLayoutManager(context)
        myTicketsRv?.adapter =
            TicketPresentAdapter(this.mvpDelegate, object :
                TicketPresentListener {
                override fun click(ticket: Ticket) {

                }

                override fun click(ticket: Ticket, ticketName: String) {
                    (activity as MainActivity).showMyTicketInfoFragment(ticket,ticketName)
                }
            }, arrayListOf())
    }

    override fun setTickets(ticketSales: ArrayList<Ticket>) {
        (myTicketsRv?.adapter as? TicketPresentAdapter)?.setData(ticketSales)
    }

    override fun showProgressBar() {
        progressBar?.visibility = View.VISIBLE
    }

    override fun hideProgressBar() {
        progressBar?.visibility = View.GONE
    }

    override fun showToast(text: String) {
        Toast.makeText(activity, text, Toast.LENGTH_SHORT).show()
    }
}
