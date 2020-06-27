package io.moonshard.moonshard.ui.fragments.profile.wallet.transacations

import android.os.Bundle
import android.util.Log
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

    private var fromEventScreen=false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_success_transaction, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            fromEventScreen = it.getBoolean("fromEventScreen")
        }

        closeBtn?.setSafeOnClickListener {
            if(fromEventScreen){
               parentFragmentManager.popBackStack("TransferWalletFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE)
            }else{
                activity!!.supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            }
        }

        closeCrossBtn?.setSafeOnClickListener {
            if(fromEventScreen){
                parentFragmentManager.popBackStack("TransferWalletFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE)
            }else{
                activity!!.supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            }
        }
    }
}
