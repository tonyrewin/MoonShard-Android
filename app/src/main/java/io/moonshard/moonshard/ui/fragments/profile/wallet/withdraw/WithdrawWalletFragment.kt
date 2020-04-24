package io.moonshard.moonshard.ui.fragments.profile.wallet.withdraw

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager

import io.moonshard.moonshard.R
import io.moonshard.moonshard.common.utils.setSafeOnClickListener
import io.moonshard.moonshard.presentation.presenter.profile.wallet.withdraw.WithdrawWalletPresenter
import io.moonshard.moonshard.presentation.view.profile.wallet.withdraw.WithdrawWalletView
import kotlinx.android.synthetic.main.fragment_withdraw_wallet.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter


class WithdrawWalletFragment : MvpAppCompatFragment(),
    WithdrawWalletView {

    @InjectPresenter
    lateinit var presenter: WithdrawWalletPresenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_withdraw_wallet, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //important for edit text
        activity!!.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        backBtn?.setSafeOnClickListener {
            fragmentManager?.popBackStack()
        }

        moneyValue.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                if(s.isEmpty()){
                    nextBtn.setBackgroundResource(R.drawable.ic_fill_up_disable)
                }else{
                    nextBtn.setBackgroundResource(R.drawable.ic_fill_up_enable)
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
            }
        })
    }
}
