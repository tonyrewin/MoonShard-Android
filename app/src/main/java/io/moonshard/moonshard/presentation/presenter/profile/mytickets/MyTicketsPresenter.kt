package io.moonshard.moonshard.presentation.presenter.profile.mytickets

import com.example.moonshardwallet.MainService
import io.moonshard.moonshard.presentation.view.profile.my_tickets.MyTicketsView
import moxy.InjectViewState
import moxy.MvpPresenter

@InjectViewState
class MyTicketsPresenter : MvpPresenter<MyTicketsView>() {

    fun getMyTickets(){
        val myTickets =  MainService.getBuyTicketSErvice().myTickets
        viewState.setTickets(myTickets)
    }

}