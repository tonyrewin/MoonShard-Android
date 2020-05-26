package io.moonshard.moonshard.presentation.presenter.chat.info.tickets

import com.example.moonshardwallet.MainService
import io.moonshard.moonshard.presentation.view.chat.info.tickets.ManageTypesTicketView
import moxy.InjectViewState
import moxy.MvpPresenter

@InjectViewState
class ManageTypesTicketPresenter : MvpPresenter<ManageTypesTicketView>() {


    fun getTypesTicket(eventJid:String){
       val typesTicket =  MainService.getBuyTicketSErvice().getTickets(eventJid)
        viewState.setTypesTicket(typesTicket)
        var test = ""
    }
}