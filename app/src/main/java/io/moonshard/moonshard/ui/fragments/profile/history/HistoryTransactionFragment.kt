package io.moonshard.moonshard.ui.fragments.profile.history

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager

import io.moonshard.moonshard.R
import io.moonshard.moonshard.common.utils.setSafeOnClickListener
import io.moonshard.moonshard.models.wallet.ListItem
import io.moonshard.moonshard.presentation.presenter.profile.history.HistoryTransactionPresenter
import io.moonshard.moonshard.presentation.view.profile.history.HistoryTransactionView
import io.moonshard.moonshard.ui.adapters.wallet.TransactionsWalletAdapter
import io.moonshard.moonshard.ui.adapters.wallet.TransactionsWalletListener
import kotlinx.android.synthetic.main.fragment_history_transaction.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import java.util.*


class HistoryTransactionFragment : MvpAppCompatFragment(),
    HistoryTransactionView {

    @InjectPresenter
    lateinit var presenter: HistoryTransactionPresenter

    val dateAndTime = Calendar.getInstance()

    // установка обработчика выбора даты
    var d: DatePickerDialog.OnDateSetListener =
        DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            dateAndTime.set(Calendar.YEAR, year)
            dateAndTime.set(Calendar.MONTH, monthOfYear)
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            //setDate(dayOfMonth, monthOfYear)

            presenter.setFilterDate(dateAndTime)

            (rv?.adapter as? TransactionsWalletAdapter)?.notifyDataSetChanged()
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history_transaction, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        calendarBtn?.setSafeOnClickListener {
            DatePickerDialog(
                activity!!, d,
                dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH)
            )
                .show()

            setDate(dateAndTime.get(Calendar.DAY_OF_MONTH), dateAndTime.get(Calendar.MONTH))
            calendarBtn?.visibility = View.GONE
            dateLayout?.visibility = View.VISIBLE
        }



        backBtn?.setSafeOnClickListener {
            fragmentManager?.popBackStack()
        }

        dateLayout?.setSafeOnClickListener {
            presenter.disableFilterDate()
            dateLayout?.visibility = View.GONE
            calendarBtn?.visibility = View.VISIBLE
        }

        initFilterBtn()
        initAdapter()

        presenter.getTransactions()
    }

    fun initFilterBtn(){
        topUpWalletBtn?.setOnClickListener {
            if(topUpWalletBtn.background.constantState == ContextCompat.getDrawable(context!!, R.drawable.wallet_button_active)?.constantState){
                topUpWalletBtn?.setBackgroundResource(R.drawable.wallet_button_default)
                topUpWalletTv.setTextColor(Color.parseColor("#333333"))

                presenter.topUpFilter(false)
            }else{
                writeOffBtn?.setBackgroundResource(R.drawable.wallet_button_default)
                writeOffTv.setTextColor(Color.parseColor("#333333"))

                topUpWalletBtn?.setBackgroundResource(R.drawable.wallet_button_active)
                topUpWalletTv.setTextColor(Color.parseColor("#FFFFFF"))
                presenter.topUpFilter(true)
            }
        }

        writeOffBtn?.setOnClickListener {
            if(writeOffBtn.background.constantState == ContextCompat.getDrawable(context!!, R.drawable.wallet_button_active)?.constantState){
                writeOffBtn?.setBackgroundResource(R.drawable.wallet_button_default)
                writeOffTv.setTextColor(Color.parseColor("#333333"))
                presenter.writeOffFilter(false)

            }else{
                topUpWalletBtn?.setBackgroundResource(R.drawable.wallet_button_default)
                topUpWalletTv.setTextColor(Color.parseColor("#333333"))

                writeOffBtn?.setBackgroundResource(R.drawable.wallet_button_active)
                writeOffTv.setTextColor(Color.parseColor("#FFFFFF"))

                presenter.writeOffFilter(true)

            }
        }
    }

    fun initAdapter(){
        rv?.layoutManager = LinearLayoutManager(context)
        rv?.adapter = TransactionsWalletAdapter(object : TransactionsWalletListener {
            override fun click() {
                val addPhotoBottomDialogFragment =
                    InfoTransactionBottomDialogFragment()
                addPhotoBottomDialogFragment.show(activity!!.supportFragmentManager, "add_photo_dialog_fragment")
            }
        }, arrayListOf())
    }

   override fun setData(transitions: List<ListItem>){
        (rv?.adapter as? TransactionsWalletAdapter)?.setTransaction(transitions)
    }

    private fun setDate(dayOfMonth: Int, month: Int) {
        when (month) {
            0 -> {
                dateTv.text = "$dayOfMonth янв"
            }
            1 -> {
                dateTv.text = "$dayOfMonth фев"

            }
            2 -> {
                dateTv.text = "$dayOfMonth мар"

            }
            3 -> {
                dateTv.text = "$dayOfMonth апр"

            }
            4 -> {
                dateTv.text = "$dayOfMonth мая"

            }
            5 -> {
                dateTv.text = "$dayOfMonth июн"

            }
            6 -> {
                dateTv.text = "$dayOfMonth июля"

            }
            7 -> {
                dateTv.text = "$dayOfMonth авг"

            }
            8 -> {
                dateTv.text = "$dayOfMonth сент"

            }
            9 -> {
                dateTv.text = "$dayOfMonth окт"

            }
            10 -> {
                dateTv.text = "$dayOfMonth нояб"
            }
            11 -> {
                dateTv.text = "$dayOfMonth дек"
            }
        }
    }
}
