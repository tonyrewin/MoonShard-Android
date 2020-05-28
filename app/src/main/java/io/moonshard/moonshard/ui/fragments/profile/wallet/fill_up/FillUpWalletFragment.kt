package io.moonshard.moonshard.ui.fragments.profile.wallet.fill_up

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import androidx.fragment.app.Fragment
import com.example.moonshardwallet.MainService

import io.moonshard.moonshard.R
import io.moonshard.moonshard.common.utils.setSafeOnClickListener
import io.moonshard.moonshard.presentation.presenter.profile.wallet.fill_up.FillUpWalletPresenter
import io.moonshard.moonshard.presentation.view.profile.wallet.fill_up.FillUpWalletView
import kotlinx.android.synthetic.main.fragment_fill_up_wallet.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter


class FillUpWalletFragment : MvpAppCompatFragment(),
    FillUpWalletView {

    @InjectPresenter
    lateinit var presenter: FillUpWalletPresenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_fill_up_wallet, container, false)
        return view
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
        balanceTv?.text = MainService.getWalletService().balance + " ₽"

    }

}
