package io.moonshard.moonshard.ui.fragments.profile.wallet.transacations

import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import io.moonshard.moonshard.R
import io.moonshard.moonshard.common.utils.setSafeOnClickListener
import io.moonshard.moonshard.presentation.presenter.profile.wallet.transactions.ConfirmTransactionPresenter
import io.moonshard.moonshard.presentation.view.profile.wallet.transactions.ConfirmTransactionView
import kotlinx.android.synthetic.main.fragment_confirm_transaction.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter


class ConfirmTransactionFragment : MvpAppCompatFragment(),
    ConfirmTransactionView {

    @InjectPresenter
    lateinit var presenter: ConfirmTransactionPresenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_confirm_transaction, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

       // notCorrectPassword

    }
}
