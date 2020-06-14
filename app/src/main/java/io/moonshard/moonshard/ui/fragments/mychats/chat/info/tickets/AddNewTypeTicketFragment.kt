package io.moonshard.moonshard.ui.fragments.mychats.chat.info.tickets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import io.moonshard.moonshard.R
import io.moonshard.moonshard.common.utils.setSafeOnClickListener
import io.moonshard.moonshard.presentation.presenter.chat.info.tickets.AddNewTypeTicketPresenter
import io.moonshard.moonshard.presentation.view.chat.info.tickets.AddNewTypeTicketView
import kotlinx.android.synthetic.main.fragment_add_new_type_ticket.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter


class AddNewTypeTicketFragment : MvpAppCompatFragment(),
    AddNewTypeTicketView {

    @InjectPresenter
    lateinit var presenter: AddNewTypeTicketPresenter

    var idChat = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_new_type_ticket, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            idChat = it.getString("chatId")
        }

        saveBtn?.setSafeOnClickListener {
            presenter.addNewEvent(typeEt.text.toString(),priceEt.text.toString(),limitEt.text.toString(),idChat)
        }

        readyBtn?.setSafeOnClickListener {
            presenter.addNewEvent(typeEt.text.toString(),priceEt.text.toString(),limitEt.text.toString(),idChat)
        }

        backBtn?.setSafeOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    override fun back(){
        parentFragmentManager.popBackStack()
    }

    override fun showProgressBar(){
        progressBar?.visibility = View.VISIBLE
    }

    override fun hideProgressBar(){
        progressBar?.visibility = View.GONE
    }

    override fun showToast(text:String) {
        Toast.makeText(activity, text, Toast.LENGTH_SHORT).show()
    }
}
