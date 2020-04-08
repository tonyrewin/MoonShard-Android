package io.moonshard.moonshard.ui.fragments.profile.mytickets

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import io.moonshard.moonshard.R
import io.moonshard.moonshard.presentation.presenter.profile.mytickets.MyTicketsPresenter
import io.moonshard.moonshard.presentation.view.profile.my_tickets.MyTicketsView
import kotlinx.android.synthetic.main.my_ticket_item.*
import moxy.InjectViewState
import moxy.MvpAppCompatFragment
import moxy.MvpPresenter
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
}
