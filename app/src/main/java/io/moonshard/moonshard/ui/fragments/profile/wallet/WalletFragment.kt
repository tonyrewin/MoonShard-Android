package io.moonshard.moonshard.ui.fragments.profile.wallet

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat

import io.moonshard.moonshard.R
import io.moonshard.moonshard.common.utils.setSafeOnClickListener
import io.moonshard.moonshard.ui.activities.MainActivity
import kotlinx.android.synthetic.main.fragment_wallet.*


class WalletFragment : Fragment() {


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

        initFilterBtn()

        backBtn?.setSafeOnClickListener {
            fragmentManager?.popBackStack()
        }

        fillUpWalletBtn?.setSafeOnClickListener {
            (activity as MainActivity).showFillUpWalletFragment()
        }

        withDrawBtn?.setSafeOnClickListener {
            (activity as MainActivity).showWithdrawWalletFragment()
        }


        transferLayout?.setOnClickListener {
            (activity as MainActivity).showTransferWalletFragment()
        }
    }

    fun initFilterBtn(){
        topUpWalletBtn?.setOnClickListener {
            if(topUpWalletBtn.background.constantState == ContextCompat.getDrawable(context!!, R.drawable.wallet_button_active)?.constantState){
                topUpWalletBtn?.setBackgroundResource(R.drawable.wallet_button_default)
                topUpWalletTv.setTextColor(Color.parseColor("#333333"))
            }else{
                writeOffBtn?.setBackgroundResource(R.drawable.wallet_button_default)
                writeOffTv.setTextColor(Color.parseColor("#333333"))

                topUpWalletBtn?.setBackgroundResource(R.drawable.wallet_button_active)
                topUpWalletTv.setTextColor(Color.parseColor("#FFFFFF"))
            }
        }

        writeOffBtn?.setOnClickListener {
            if(writeOffBtn.background.constantState == ContextCompat.getDrawable(context!!, R.drawable.wallet_button_active)?.constantState){
                writeOffBtn?.setBackgroundResource(R.drawable.wallet_button_default)
                writeOffTv.setTextColor(Color.parseColor("#333333"))
            }else{
                topUpWalletBtn?.setBackgroundResource(R.drawable.wallet_button_default)
                topUpWalletTv.setTextColor(Color.parseColor("#333333"))

                writeOffBtn?.setBackgroundResource(R.drawable.wallet_button_active)
                writeOffTv.setTextColor(Color.parseColor("#FFFFFF"))

            }
        }
    }

}
