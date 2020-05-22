package io.moonshard.moonshard.ui.fragments.profile.wallet.transfer

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
import io.moonshard.moonshard.presentation.presenter.profile.wallet.transfer.TransferRecipientDialogPresenter
import io.moonshard.moonshard.presentation.view.profile.wallet.transfer.TransferRecipientDialogView
import io.moonshard.moonshard.ui.adapters.wallet.RecipientWalletAdapter
import io.moonshard.moonshard.ui.adapters.wallet.RecipientWalletListener
import kotlinx.android.synthetic.main.fragment_transfer_recipient_dialog.*
import moxy.MvpAppCompatDialogFragment
import moxy.presenter.InjectPresenter
import org.jivesoftware.smack.roster.RosterEntry


class TransferRecipientDialogFragment : MvpAppCompatDialogFragment(), TransferRecipientDialogView {

    @InjectPresenter
    lateinit var presenter: TransferRecipientDialogPresenter


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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_transfer_recipient_dialog, container, false)
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
                override fun click(jid:String) {
                    //todo test
                    chooseBtn?.setOnClickListener{
                        (fragmentManager!!.findFragmentByTag("TransferWalletFragment") as? TransferWalletFragment)?.showRecipient(jid)
                        dismiss()
                    }

                }
            }, arrayListOf())
    }

    override fun showContacts(contacts: ArrayList<RosterEntry>) {
        (rv?.adapter as? RecipientWalletAdapter)?.setContacts(contacts)
    }
}
