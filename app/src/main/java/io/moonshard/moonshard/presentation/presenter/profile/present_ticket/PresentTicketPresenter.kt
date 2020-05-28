package io.moonshard.moonshard.presentation.presenter.profile.present_ticket

import com.example.moonshardwallet.MainService
import io.moonshard.moonshard.presentation.view.profile.present_ticket.PresentTicketView
import moxy.InjectViewState
import moxy.MvpPresenter

@InjectViewState
class PresentTicketPresenter : MvpPresenter<PresentTicketView>() {

    fun getMyTickets(){
        val myTickets =  MainService.getBuyTicketSErvice().myTickets
        viewState.setTickets(myTickets)
    }
}