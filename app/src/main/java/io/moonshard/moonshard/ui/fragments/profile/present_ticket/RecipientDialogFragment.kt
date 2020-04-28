package io.moonshard.moonshard.ui.fragments.profile.present_ticket

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import io.moonshard.moonshard.R
import io.moonshard.moonshard.presentation.presenter.profile.present_ticket.RecipientDialogPresenter
import io.moonshard.moonshard.presentation.view.profile.present_ticket.RecipientDialogiView
import io.moonshard.moonshard.ui.adapters.wallet.RecipientWalletAdapter
import io.moonshard.moonshard.ui.adapters.wallet.RecipientWalletListener
import kotlinx.android.synthetic.main.fragment_recipient_dialog.*
import moxy.MvpAppCompatDialogFragment
import moxy.presenter.InjectPresenter
import org.jivesoftware.smack.roster.RosterEntry


class RecipientDialogFragment : MvpAppCompatDialogFragment(),RecipientDialogiView {

    @InjectPresenter
    lateinit var presenter: RecipientDialogPresenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recipient_dialog, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val metrics = DisplayMetrics()
        activity!!.windowManager.defaultDisplay.getMetrics(metrics)
        dialog?.window?.setGravity(Gravity.BOTTOM)
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            (metrics.heightPixels * 0.87).toInt()
        ) // here i have fragment height 30% of window's height you can set it as per your requirement

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.attributes?.windowAnimations = R.style.DialogAnimation
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecipientAdapter()
        presenter.getContacts()

        chooseBtn?.setOnClickListener{
            dismiss()
        }

        cancelBtn?.setOnClickListener{
            dismiss()
        }
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

    override fun showContacts(contacts: ArrayList<RosterEntry>) {
        (rv?.adapter as? RecipientWalletAdapter)?.setContacts(contacts)
    }

}