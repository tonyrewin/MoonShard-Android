package io.moonshard.moonshard.ui.fragments.profile.wallet.transacations

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import io.moonshard.moonshard.R
import io.moonshard.moonshard.presentation.presenter.profile.wallet.transactions.SuccessTransactionPresenter
import io.moonshard.moonshard.presentation.view.profile.wallet.transactions.SuccessTransactionView
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter


class SuccessTransactionFragment : MvpAppCompatFragment(),
    SuccessTransactionView {

    @InjectPresenter
    lateinit var presenter: SuccessTransactionPresenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_success_transaction, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}
