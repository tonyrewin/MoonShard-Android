package io.moonshard.moonshard.ui.fragments.profile.wallet.transacations

import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.adorsys.android.securestoragelibrary.SecurePreferences
import io.moonshard.moonshard.R
import io.moonshard.moonshard.common.utils.setSafeOnClickListener
import io.moonshard.moonshard.presentation.presenter.profile.wallet.transactions.ConfirmTransactionPresenter
import io.moonshard.moonshard.presentation.view.profile.wallet.transactions.ConfirmTransactionView
import io.moonshard.moonshard.ui.fragments.profile.wallet.transfer.TransferWalletFragment
import io.moonshard.moonshard.ui.fragments.profile.wallet.withdraw.WithdrawWalletFragment
import kotlinx.android.synthetic.main.fragment_confirm_transaction.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter


class ConfirmTransactionFragment : MvpAppCompatFragment(),
    ConfirmTransactionView {

    @InjectPresenter
    lateinit var presenter: ConfirmTransactionPresenter

    private var fromEventScreen=false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_confirm_transaction, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            fromEventScreen = it.getBoolean("fromEventScreen")
        }

        var isSecurity = true
        visiblePassBtn?.setSafeOnClickListener {
            if (isSecurity) {
                editPassword?.transformationMethod = null
                visiblePassBtn?.setImageResource(R.drawable.ic_pass_on)
                isSecurity = false
            } else {
                editPassword?.transformationMethod = PasswordTransformationMethod()
                visiblePassBtn?.setImageResource(R.drawable.ic_pass_off)
                isSecurity = true
            }
        }

        closeCrossBtn?.setSafeOnClickListener{
            parentFragmentManager.popBackStack()
        }

        buyTicketBtn?.setSafeOnClickListener {
            val password = SecurePreferences.getStringValue("pass", null)
            if (password == editPassword.text.toString()) {
                when {
                    parentFragmentManager.findFragmentByTag("WithdrawWalletFragment") != null -> {
                        val fragment = (parentFragmentManager.findFragmentByTag("WithdrawWalletFragment") as? WithdrawWalletFragment)
                        fragment?.confirmTransaction()
                        parentFragmentManager.popBackStack()
                    }
                    parentFragmentManager.findFragmentByTag("TransferWalletFragment") != null -> {
                        val fragment = (parentFragmentManager.findFragmentByTag("TransferWalletFragment") as? TransferWalletFragment)
                        fragment?.confirmTransaction()
                        parentFragmentManager.popBackStack()
                    }
                    else -> {

                    }
                }
            }else{
                notCorrectPassword?.visibility = View.VISIBLE
            }
        }
    }
}
