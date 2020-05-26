package io.moonshard.moonshard.ui.fragments.mychats.chat.info.tickets

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import io.moonshard.moonshard.R
import io.moonshard.moonshard.common.utils.setSafeOnClickListener
import io.moonshard.moonshard.presentation.presenter.chat.info.tickets.ManageTypesTicketPresenter
import io.moonshard.moonshard.presentation.view.chat.info.tickets.ManageTypesTicketView
import io.moonshard.moonshard.ui.fragments.mychats.chat.MainChatFragment
import kotlinx.android.synthetic.main.fragment_manage_types_ticket.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter


class ManageTypesTicketFragment : MvpAppCompatFragment(),
    ManageTypesTicketView {

    @InjectPresenter
    lateinit var presenter: ManageTypesTicketPresenter

    var idChat = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manage_types_ticket, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            idChat = it.getString("chatId")
        }

        backBtn?.setSafeOnClickListener {
            parentFragmentManager.popBackStack()
        }

        addNewTicketBtn?.setSafeOnClickListener {
            (parentFragment as? MainChatFragment)?.showAddNewTypeTicketScreen(idChat)
        }
    }
}
