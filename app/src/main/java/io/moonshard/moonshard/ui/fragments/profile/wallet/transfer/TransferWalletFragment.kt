package io.moonshard.moonshard.ui.fragments.profile.wallet.transfer

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior

import io.moonshard.moonshard.R
import io.moonshard.moonshard.ui.adapters.tickets.TicketListener
import io.moonshard.moonshard.ui.adapters.tickets.TicketsAdapter
import io.moonshard.moonshard.ui.adapters.wallet.RecipientWalletAdapter
import io.moonshard.moonshard.ui.adapters.wallet.RecipientWalletListener
import kotlinx.android.synthetic.main.fragment_transfer_wallet.*
import kotlinx.android.synthetic.main.recipient_bottom_sheet.*


class TransferWalletFragment : Fragment() {

    var sheetInfoBehavior: BottomSheetBehavior<View>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //important for edit text
        activity!!.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_transfer_wallet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        val llInfoBottomSheet = view.findViewById<NestedScrollView>(R.id.infoBottomSheet)
        sheetInfoBehavior = BottomSheetBehavior.from(llInfoBottomSheet)


        chooseMember?.setOnClickListener {
            sheetInfoBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
        }

        chooseBtn?.setOnClickListener{
            sheetInfoBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        cancelBtn?.setOnClickListener{
            sheetInfoBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        initAdapter()
    }

    private fun initAdapter() {
        rv?.layoutManager = LinearLayoutManager(context)
        rv?.adapter =
            RecipientWalletAdapter(object :
                RecipientWalletListener {
                override fun click() {

                }
            }, arrayListOf())
    }
}
