package io.moonshard.moonshard.ui.fragments.profile.wallet

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import io.moonshard.moonshard.R
import io.moonshard.moonshard.common.utils.setSafeOnClickListener
import io.moonshard.moonshard.models.wallet.DateItem
import io.moonshard.moonshard.models.wallet.GeneralItem
import io.moonshard.moonshard.models.wallet.ListItem
import io.moonshard.moonshard.models.wallet.PojoOfJsonArray
import io.moonshard.moonshard.presentation.presenter.profile.wallet.WalletPresenter
import io.moonshard.moonshard.presentation.view.profile.wallet.WalletView
import io.moonshard.moonshard.ui.activities.MainActivity
import io.moonshard.moonshard.ui.adapters.wallet.TransactionsWalletAdapter
import io.moonshard.moonshard.ui.adapters.wallet.TransactionsWalletListener
import io.moonshard.moonshard.ui.fragments.profile.wallet.history.InfoTransactionBottomDialogFragment
import kotlinx.android.synthetic.main.fragment_wallet.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter


class WalletFragment : MvpAppCompatFragment(),
    WalletView {

    @InjectPresenter
    lateinit var presenter: WalletPresenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_wallet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).hideBottomNavigationBar()


        backBtn?.setSafeOnClickListener {
            fragmentManager?.popBackStack()
        }

        fillUpWalletBtn?.setSafeOnClickListener {
            (activity as MainActivity).showFillUpWalletFragment()
        }

        withDrawBtn?.setSafeOnClickListener {
            (activity as MainActivity).showWithdrawWalletFragment()
        }

        historyBtn?.setSafeOnClickListener {
            (activity as MainActivity).showHistoryTransactionScreen()
        }

        transferLayout?.setOnClickListener {
            (activity as MainActivity).showTransferWalletFragment()
        }
    }
}
