package io.moonshard.moonshard.ui.fragments.profile.present_ticket

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moonshardwallet.MainService
import com.example.moonshardwallet.models.Ticket
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import io.moonshard.moonshard.R
import io.moonshard.moonshard.presentation.presenter.profile.present_ticket.PresentTicketPresenter
import io.moonshard.moonshard.presentation.view.profile.present_ticket.PresentTicketView
import io.moonshard.moonshard.ui.activities.MainActivity
import io.moonshard.moonshard.ui.adapters.profile.present.TicketPresentAdapter
import io.moonshard.moonshard.ui.adapters.profile.present.TicketPresentListener
import kotlinx.android.synthetic.main.fragment_present_ticket.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter


class PresentTicketFragment : MvpAppCompatFragment(), PresentTicketView {

    @InjectPresenter
    lateinit var presenter: PresentTicketPresenter

    var sheetInfoBehavior: BottomSheetBehavior<View>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity!!.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_present_ticket, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? MainActivity)?.hideBottomNavigationBar()

        backBtn?.setOnClickListener {
            fragmentManager?.popBackStack()
        }
        initTypeTicketPresentAdapter()

        presenter.getMyTickets()
    }

    private fun initTypeTicketPresentAdapter() {
        ticketPresentRv?.layoutManager = LinearLayoutManager(context)
        ticketPresentRv?.adapter =
            TicketPresentAdapter(this.mvpDelegate,object :
                TicketPresentListener {
                override fun click(ticket: Ticket) {
                    MainService.getBuyTicketService().presentTicket("0xb4a31ab401bc17feb7d9697792c73cfe58546a3b",ticket.ticketId)

                    val ticketJson = Gson().toJson(ticket)
                    val bundle = Bundle()
                    bundle.putString("ticket", ticketJson)
                    val addPhotoBottomDialogFragment = RecipientDialogFragment()
                    addPhotoBottomDialogFragment.show(
                        activity!!.supportFragmentManager,
                        "RecipientDialogFragment"
                    )                }
            }, arrayListOf())
    }

    override fun setTickets(ticketSales: ArrayList<Ticket>) {
        (ticketPresentRv?.adapter as? TicketPresentAdapter)?.setData(ticketSales)
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
