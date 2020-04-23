package io.moonshard.moonshard.ui.fragments.profile.present_ticket

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior

import io.moonshard.moonshard.R
import io.moonshard.moonshard.common.utils.Utils
import io.moonshard.moonshard.ui.adapters.profile.present.TypeTicketPresentAdapter
import io.moonshard.moonshard.ui.adapters.profile.present.TypeTicketPresentListener
import io.moonshard.moonshard.ui.adapters.wallet.RecipientWalletAdapter
import io.moonshard.moonshard.ui.adapters.wallet.RecipientWalletListener
import kotlinx.android.synthetic.main.fragment_type_ticket_present.*
import kotlinx.android.synthetic.main.little_ticket_item.view.*
import kotlinx.android.synthetic.main.recipient_bottom_sheet.*


class TypeTicketPresentFragment : Fragment() {

    var sheetInfoBehavior: BottomSheetBehavior<View>? = null



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_type_ticket_present, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val llInfoBottomSheet = view.findViewById<NestedScrollView>(R.id.bottomSheet)
        sheetInfoBehavior = BottomSheetBehavior.from(llInfoBottomSheet)

        chooseBtn?.setOnClickListener{
            backBtn.isClickable = true
            layoutToolbar?.alpha = 1f
            toolBarTittle?.text = "Подарить билеты"
            layoutToolbar?.setBackgroundColor(Color.parseColor("#ffffffff"))
            mainLayout?.setBackgroundColor(Color.parseColor("#FAFAFA"))
            sheetInfoBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        cancelBtn?.setOnClickListener{
            backBtn.isClickable = true
            layoutToolbar?.alpha = 1f
            toolBarTittle?.text = "Подарить билеты"
            layoutToolbar?.setBackgroundColor(Color.parseColor("#ffffffff"))
            mainLayout?.setBackgroundColor(Color.parseColor("#FAFAFA"))
            sheetInfoBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        initTypeTicketPresentAdapter()
        initRecipientAdapter()

        backBtn?.setOnClickListener {
            fragmentManager?.popBackStack()
        }
    }

    private fun initTypeTicketPresentAdapter(){
        typeTicketPresentRv?.layoutManager = LinearLayoutManager(context)
        typeTicketPresentRv?.adapter =
            TypeTicketPresentAdapter(object :
                TypeTicketPresentListener {
                override fun click() {
                    backBtn.isClickable = false
                    toolBarTittle?.text = "Отправить билет"
                    toolBar?.setBackgroundColor(Color.rgb(242, 242, 242))
                    layoutToolbar?.alpha = 0.5f
                    mainLayout?.setBackgroundColor(Color.rgb(242, 242, 242))

                    //  layoutToolbar?.setBackgroundColor(Color.rgb(242, 242, 242))
                  //  mainLayout?.setBackgroundColor(Color.rgb(242, 242, 242))
                    //layoutToolbar?.elevation = Utils.convertDpToPixel(4F,context).toFloat()
                    sheetInfoBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
                }
            }, arrayListOf())
    }

    private fun initRecipientAdapter() {
        rv?.layoutManager = LinearLayoutManager(context)
        rv?.adapter =
            RecipientWalletAdapter(object :
                RecipientWalletListener {
                override fun click() {

                }
            }, arrayListOf())
    }
}
