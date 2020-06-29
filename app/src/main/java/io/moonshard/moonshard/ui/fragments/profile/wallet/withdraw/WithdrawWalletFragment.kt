package io.moonshard.moonshard.ui.fragments.profile.wallet.withdraw

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import io.moonshard.moonshard.R
import io.moonshard.moonshard.common.utils.setSafeOnClickListener
import io.moonshard.moonshard.presentation.presenter.profile.wallet.withdraw.WithdrawWalletPresenter
import io.moonshard.moonshard.presentation.view.profile.wallet.withdraw.WithdrawWalletView
import io.moonshard.moonshard.ui.activities.MainActivity
import io.moonshard.moonshard.ui.fragments.mychats.chat.MainChatFragment
import kotlinx.android.synthetic.main.fragment_withdraw_wallet.*
import kotlinx.android.synthetic.main.fragment_withdraw_wallet.backBtn
import kotlinx.android.synthetic.main.fragment_withdraw_wallet.balanceTv
import kotlinx.android.synthetic.main.fragment_withdraw_wallet.moneyValue
import kotlinx.android.synthetic.main.fragment_withdraw_wallet.nextBtn
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter


class WithdrawWalletFragment : MvpAppCompatFragment(),
    WithdrawWalletView {

    @InjectPresenter
    lateinit var presenter: WithdrawWalletPresenter

    private var fromEventScreen=false


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
        activity!!.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            fromEventScreen = it.getBoolean("fromEventScreen")
        }

        backBtn?.setSafeOnClickListener {
            parentFragmentManager.popBackStack()
        }

        moneyValue.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                if (s.isEmpty()) {
                    nextBtn.setBackgroundResource(R.drawable.ic_fill_up_disable)
                } else {
                    nextBtn.setBackgroundResource(R.drawable.ic_fill_up_enable)
                }
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
            }
        })

        nextBtn?.setSafeOnClickListener {
            if(fromEventScreen){
                (parentFragment as? MainChatFragment)?.showConfirmTransactionFragment(this)
            }else{
                (activity as MainActivity).showConfirmTransactionFragment(this)
            }
        }

        presenter.getBalance()
    }

    override fun showBalance(balance: String) {
        balanceTv?.text = "$balance ₽"
    }

    fun confirmTransaction() {
        presenter.cashOut(cardNumberEt.text.toString(), moneyValue.text.toString())
    }

    override fun showSuccessScreen() {
        if(fromEventScreen){
            (parentFragment as? MainChatFragment)?.showSuccessTransactionFragment(this)
        }else{
            (activity as? MainActivity)?.showSuccessTransactionFragment(this)
        }
    }

    override fun showToast(text: String) {
        Toast.makeText(context!!, text, Toast.LENGTH_SHORT).show()
    }

    override fun showProgressBar() {
        progressBar?.visibility = View.VISIBLE
    }

    override fun hideProgressBar() {
        progressBar?.visibility = View.GONE
    }
}
