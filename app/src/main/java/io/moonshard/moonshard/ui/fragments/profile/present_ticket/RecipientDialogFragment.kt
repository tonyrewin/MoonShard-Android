package io.moonshard.moonshard.ui.fragments.profile.present_ticket

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moonshardwallet.models.Ticket
import com.google.gson.Gson
import com.jakewharton.rxbinding3.widget.afterTextChangeEvents
import io.moonshard.moonshard.R
import io.moonshard.moonshard.models.jabber.Recipient
import io.moonshard.moonshard.presentation.presenter.profile.present_ticket.RecipientDialogPresenter
import io.moonshard.moonshard.presentation.view.profile.present_ticket.RecipientDialogiView
import io.moonshard.moonshard.ui.adapters.wallet.RecipientWalletAdapter
import io.moonshard.moonshard.ui.adapters.wallet.RecipientWalletListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_recipient_dialog.*
import kotlinx.android.synthetic.main.fragment_recipient_dialog.cancelBtn
import kotlinx.android.synthetic.main.fragment_recipient_dialog.chooseBtn
import kotlinx.android.synthetic.main.fragment_recipient_dialog.editSearch
import kotlinx.android.synthetic.main.fragment_recipient_dialog.progressBar
import kotlinx.android.synthetic.main.fragment_recipient_dialog.rv
import kotlinx.android.synthetic.main.fragment_transfer_recipient_dialog.*
import moxy.MvpAppCompatDialogFragment
import moxy.presenter.InjectPresenter


class RecipientDialogFragment : MvpAppCompatDialogFragment(), RecipientDialogiView {

    @InjectPresenter
    lateinit var presenter: RecipientDialogPresenter

    private var disposible: Disposable? = null


    var ticket: Ticket? = null
    var userJid: String? = null

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

        arguments?.let {
            val ticketJson = it.getString("ticket")
            ticket = Gson().fromJson(ticketJson, Ticket::class.java)
        }

        initRecipientAdapter()

        presenter.getContactsRx()

        chooseBtn?.setOnClickListener {
            presenter.sendTicketAsPresent(userJid, ticket!!.ticketId)
        }

        cancelBtn?.setOnClickListener {
            dismiss()
        }

        disposible = editSearch?.afterTextChangeEvents()
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe {
                try {
                    presenter.setFilter(it.editable.toString())
                } catch (e: Exception) {
                    com.orhanobut.logger.Logger.d(e)
                }
            }
    }

    private fun initRecipientAdapter() {
        rv?.layoutManager = LinearLayoutManager(context)
        rv?.adapter =
            RecipientWalletAdapter(object :
                RecipientWalletListener {
                override fun click(asUnescapedString: String) {
                    userJid = asUnescapedString
                }
            }, arrayListOf())
    }

    override fun onDataChange() {
        (rv?.adapter as? RecipientWalletAdapter)?.notifyDataSetChanged()
    }

    override fun showContacts(contacts: ArrayList<Recipient>) {
        (rv?.adapter as? RecipientWalletAdapter)?.setContacts(contacts)
    }

    override fun back() {
        dismiss()
        activity?.supportFragmentManager?.popBackStack()
    }

    override fun dismissBack() {
        dismiss()
    }

    override fun showToast(text: String) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }

    override fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
    }

    override fun hideProgressBar() {
        progressBar.visibility = View.GONE
    }
}
