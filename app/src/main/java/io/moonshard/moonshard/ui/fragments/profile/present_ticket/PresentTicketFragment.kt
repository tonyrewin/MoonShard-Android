package io.moonshard.moonshard.ui.fragments.profile.present_ticket

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moonshardwallet.models.Ticket
import com.google.android.material.bottomsheet.BottomSheetBehavior
import io.moonshard.moonshard.R
import io.moonshard.moonshard.presentation.presenter.profile.present_ticket.PresentTicketPresenter
import io.moonshard.moonshard.presentation.view.profile.present_ticket.PresentTicketView
import io.moonshard.moonshard.ui.activities.MainActivity
import io.moonshard.moonshard.ui.adapters.profile.present.TicketPresentAdapter
import io.moonshard.moonshard.ui.adapters.profile.present.TicketPresentListener
import io.moonshard.moonshard.ui.adapters.profile.present.TypeTicketPresentAdapter
import io.moonshard.moonshard.ui.adapters.profile.present.TypeTicketPresentListener
import io.moonshard.moonshard.ui.adapters.wallet.RecipientWalletAdapter
import kotlinx.android.synthetic.main.fragment_present_ticket.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import org.jivesoftware.smack.roster.RosterEntry


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
    }

    private fun initTypeTicketPresentAdapter() {
        ticketPresentRv?.layoutManager = LinearLayoutManager(context)
        ticketPresentRv?.adapter =
            TicketPresentAdapter(object :
                TicketPresentListener {
                override fun click(ticket: Ticket) {
                    val addPhotoBottomDialogFragment = RecipientDialogFragment()
                    addPhotoBottomDialogFragment.show(
                        activity!!.supportFragmentManager,
                        "RecipientDialogFragment"
                    )                }
            }, arrayListOf())
    }

    override fun setTickets(ticketSales: ArrayList<Ticket>) {
        (ticketPresentRv?.adapter as? TicketPresentAdapter)?.update(ticketSales)
    }}
