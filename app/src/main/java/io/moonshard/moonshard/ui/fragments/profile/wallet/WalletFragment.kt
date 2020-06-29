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
import io.moonshard.moonshard.ui.fragments.mychats.chat.MainChatFragment
import kotlinx.android.synthetic.main.fragment_wallet.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter


class WalletFragment : MvpAppCompatFragment(),
    WalletView {

    @InjectPresenter
    lateinit var presenter: WalletPresenter

    private var fromEventScreen=false

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

        arguments?.let {
            fromEventScreen = it.getBoolean("fromEventScreen")
        }

        backBtn?.setSafeOnClickListener {
                parentFragmentManager.popBackStack()
        }

        fillUpWalletBtn?.setSafeOnClickListener {
            if(fromEventScreen){
                (parentFragment as? MainChatFragment)?.showFillUpWalletFragment()
            }else{
                (activity as MainActivity).showFillUpWalletFragment()
            }
        }

        withDrawBtn?.setSafeOnClickListener {
            if(fromEventScreen){
                (parentFragment as? MainChatFragment)?.showWithdrawWalletFragment()
            }else{
                (activity as MainActivity).showWithdrawWalletFragment()
            }
        }

        historyBtn?.setSafeOnClickListener {
            if(fromEventScreen){
                (parentFragment as? MainChatFragment)?.showHistoryTransactionScreen()
            }else{
                (activity as MainActivity).showHistoryTransactionScreen()
            }
        }

        transferLayout?.setOnClickListener {
            if(fromEventScreen){
                (parentFragment as? MainChatFragment)?.showTransferWalletFragment()
            }else{
                (activity as MainActivity).showTransferWalletFragment()
            }
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
