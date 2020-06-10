package io.moonshard.moonshard.ui.fragments.profile.wallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.moonshardwallet.MainService
import com.example.moonshardwallet.WalletService
import io.moonshard.moonshard.R
import io.moonshard.moonshard.common.utils.setSafeOnClickListener
import io.moonshard.moonshard.presentation.presenter.profile.wallet.WalletPresenter
import io.moonshard.moonshard.presentation.view.profile.wallet.WalletView
import io.moonshard.moonshard.ui.activities.MainActivity
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

        presenter.getBalance()
    }

    override fun showBalance(balance:String){
        balanceTv?.text = "$balance ₽"
    }

    override fun showToast(text:String){
        Toast.makeText(context!!, text, Toast.LENGTH_SHORT).show()
    }
}
