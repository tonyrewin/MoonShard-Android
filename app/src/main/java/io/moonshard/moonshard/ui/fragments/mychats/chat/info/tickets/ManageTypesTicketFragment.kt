package io.moonshard.moonshard.ui.fragments.mychats.chat.info.tickets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moonshardwallet.models.MyTicketSale

import io.moonshard.moonshard.R
import io.moonshard.moonshard.common.utils.setSafeOnClickListener
import io.moonshard.moonshard.presentation.presenter.chat.info.tickets.ManageTypesTicketPresenter
import io.moonshard.moonshard.presentation.view.chat.info.tickets.ManageTypesTicketView
import io.moonshard.moonshard.ui.adapters.tickets.TypesTicketAdapter
import io.moonshard.moonshard.ui.adapters.tickets.TypesTicketListener
import io.moonshard.moonshard.ui.fragments.mychats.chat.MainChatFragment
import kotlinx.android.synthetic.main.fragment_manage_types_ticket.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter


class ManageTypesTicketFragment : MvpAppCompatFragment(),
    ManageTypesTicketView {

    @InjectPresenter
    lateinit var presenter: ManageTypesTicketPresenter

    var idChat = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manage_types_ticket, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            idChat = it.getString("chatId")
        }

        backBtn?.setSafeOnClickListener {
            parentFragmentManager.popBackStack()
        }

        addNewTicketBtn?.setSafeOnClickListener {
            (parentFragment as? MainChatFragment)?.showAddNewTypeTicketScreen(idChat)
        }

        initAdapter()

        presenter.getTypesTicket(idChat)
    }

    private fun initAdapter() {
        typesTicketsRv?.layoutManager = LinearLayoutManager(context)
        typesTicketsRv?.adapter = TypesTicketAdapter(this.mvpDelegate,object : TypesTicketListener {
            override fun changeClick() {
            }
        }, arrayListOf(),idChat)
    }

    override fun setTypesTicket(ticketSales: ArrayList<MyTicketSale>) {
        (typesTicketsRv?.adapter as? TypesTicketAdapter)?.update(ticketSales)
    }

    override fun showToast(text:String){
        Toast.makeText(activity, text, Toast.LENGTH_SHORT).show()
    }
}
