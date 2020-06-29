package io.moonshard.moonshard.ui.fragments.mychats.chat.info.tickets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.moonshard.moonshard.R
import io.moonshard.moonshard.common.utils.setSafeOnClickListener
import io.moonshard.moonshard.presentation.presenter.chat.info.tickets.SuccessWalletPresenter
import io.moonshard.moonshard.presentation.view.chat.info.tickets.SuccessWalletView
import io.moonshard.moonshard.ui.fragments.mychats.chat.MainChatFragment
import kotlinx.android.synthetic.main.fragment_success_wallet.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter


class SuccessWalletFragment : MvpAppCompatFragment(),
    SuccessWalletView {

    var idChat = ""
    var moneyValue: String = ""

    @InjectPresenter
    lateinit var presenter: SuccessWalletPresenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_success_wallet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            idChat = it.getString("chatId")
            moneyValue = it.getString("moneyValue")
            showMonetValue(moneyValue)
        }

        showTicketsBtn?.setSafeOnClickListener {
            (parentFragment as? MainChatFragment)?.showMyTicketsFragment()
        }

        chatBtn?.setSafeOnClickListener {
            parentFragmentManager.popBackStack()
            parentFragmentManager.popBackStack()
            parentFragmentManager.popBackStack()
            parentFragmentManager.popBackStack()
        }

       // backBtn?.setSafeOnClickListener {
       //     parentFragmentManager.popBackStack()
      //  }
    }

    private fun showMonetValue(moneyValue: String) {
        valueWallet?.text = "Списано $moneyValue ₽ с кошелька"
    }
}
