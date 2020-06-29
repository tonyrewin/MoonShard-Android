package io.moonshard.moonshard.ui.fragments.mychats.chat.info.tickets

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.moonshard.moonshard.R
import io.moonshard.moonshard.common.utils.setSafeOnClickListener
import io.moonshard.moonshard.ui.activities.MainActivity
import io.moonshard.moonshard.ui.fragments.mychats.chat.MainChatFragment
import kotlinx.android.synthetic.main.fragment_tickets_manage.*


class TicketsManageFragment : Fragment() {

    var idChat = ""
    var typeRole: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tickets_manage, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            idChat = it.getString("chatId")
            typeRole = it.getString("typeRole")
        }

        when (typeRole) {
            "owner" -> {
                addTicketsBtn?.visibility = View.VISIBLE
                scanTicketsBtn?.visibility = View.VISIBLE
                statisticBtn?.visibility = View.VISIBLE
                walletBtn?.visibility = View.VISIBLE
            }
            "admin" -> {
                addTicketsBtn?.visibility = View.GONE
                scanTicketsBtn?.visibility = View.VISIBLE
                statisticBtn?.visibility = View.VISIBLE
                walletBtn?.visibility = View.GONE
            }
            "FaceController" -> {
                addTicketsBtn?.visibility = View.GONE
                scanTicketsBtn?.visibility = View.VISIBLE
                statisticBtn?.visibility = View.GONE
                walletBtn?.visibility = View.GONE
            }
        }

        backBtn?.setSafeOnClickListener {
            parentFragmentManager.popBackStack()
        }

        addTicketsBtn?.setSafeOnClickListener {
            (parentFragment as? MainChatFragment)?.showManageTypesTicketScreen(idChat)
        }

        scanTicketsBtn?.setSafeOnClickListener {
            (parentFragment as? MainChatFragment)?.showScanQrTicketFragment(idChat)
        }

        walletBtn?.setSafeOnClickListener {
            (parentFragment as? MainChatFragment)?.showWalletFragment()
        }

        statisticBtn?.setSafeOnClickListener {
            (parentFragment as? MainChatFragment)?.showStatisticTicketsFragment(idChat)
        }
    }
}
