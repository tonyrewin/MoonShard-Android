package io.moonshard.moonshard.ui.fragments.profile.mytickets

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.moonshard.moonshard.R
import io.moonshard.moonshard.presentation.presenter.profile.mytickets.MyTicketInfoPresenter
import io.moonshard.moonshard.presentation.view.profile.my_tickets.MyTicketInfoView
import kotlinx.android.synthetic.main.fragment_my_ticket_info.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter


class MyTicketInfoFragment: MvpAppCompatFragment(), MyTicketInfoView {

    @InjectPresenter
    lateinit var presenter: MyTicketInfoPresenter

    var isActiveAction: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_ticket_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        backBtn?.setOnClickListener {
            if (isActiveAction) {
                isActiveAction = false
                qrCode?.visibility = View.VISIBLE
               // eventEndedTv?.visibility = View.VISIBLE
                actionBtn?.visibility = View.VISIBLE

                actionLayout?.visibility = View.GONE
                deleteTicketLayout?.visibility = View.GONE
            } else {
                fragmentManager?.popBackStack()
            }
        }

        actionBtn?.setOnClickListener {
            isActiveAction = true
            qrCode?.visibility = View.GONE
           //eventEndedTv?.visibility = View.GONE
            actionBtn?.visibility = View.GONE

            actionLayout?.visibility = View.VISIBLE
            deleteTicketLayout?.visibility = View.VISIBLE
        }
    }
}
