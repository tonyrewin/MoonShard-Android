package io.moonshard.moonshard.ui.fragments.profile.wallet.transacations

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager

import io.moonshard.moonshard.R
import io.moonshard.moonshard.common.utils.setSafeOnClickListener
import io.moonshard.moonshard.presentation.presenter.profile.wallet.transactions.SuccessTransactionPresenter
import io.moonshard.moonshard.presentation.view.profile.wallet.transactions.SuccessTransactionView
import kotlinx.android.synthetic.main.fragment_success_transaction.*
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


        closeBtn?.setSafeOnClickListener {
            activity!!.supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }

        closeCrossBtn?.setSafeOnClickListener {
            activity!!.supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
    }
}
