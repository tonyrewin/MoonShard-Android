package io.moonshard.moonshard.ui.fragments.profile.present_ticket

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import io.moonshard.moonshard.R
import io.moonshard.moonshard.presentation.presenter.profile.present_ticket.TypeTicketPresentPresenter
import io.moonshard.moonshard.presentation.view.profile.present_ticket.TypeTicketPresentView
import io.moonshard.moonshard.ui.adapters.profile.present.TypeTicketPresentAdapter
import io.moonshard.moonshard.ui.adapters.profile.present.TypeTicketPresentListener
import io.moonshard.moonshard.ui.adapters.wallet.RecipientWalletAdapter
import io.moonshard.moonshard.ui.adapters.wallet.RecipientWalletListener
import kotlinx.android.synthetic.main.fragment_type_ticket_present.*
import kotlinx.android.synthetic.main.recipient_bottom_sheet.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import org.jivesoftware.smack.roster.RosterEntry


class TypeTicketPresentFragment : MvpAppCompatFragment(), TypeTicketPresentView {

    @InjectPresenter
    lateinit var presenter: TypeTicketPresentPresenter

    var sheetInfoBehavior: BottomSheetBehavior<View>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
            activity!!.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

    }

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

        sheetInfoBehavior!!.saveFlags = BottomSheetBehavior.SAVE_ALL

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

        backBtn?.setOnClickListener {
            fragmentManager?.popBackStack()
        }
        initTypeTicketPresentAdapter()
    }

    private fun initTypeTicketPresentAdapter(){
        typeTicketPresentRv?.layoutManager = LinearLayoutManager(context)
        typeTicketPresentRv?.adapter =
            TypeTicketPresentAdapter(object :
                TypeTicketPresentListener {
                override fun click() {
                    val addPhotoBottomDialogFragment = RecipientDialogFragment()
                   addPhotoBottomDialogFragment.show(activity!!.supportFragmentManager, "RecipientDialogFragment")
                }
            }, arrayListOf())
    }

    override fun showContacts(contacts: ArrayList<RosterEntry>) {
       // (rv?.adapter as? RecipientWalletAdapter)?.setContacts(contacts)
    }
}
