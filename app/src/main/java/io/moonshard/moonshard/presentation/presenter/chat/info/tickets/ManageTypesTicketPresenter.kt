package io.moonshard.moonshard.presentation.presenter.chat.info.tickets

import android.util.Log
import com.example.moonshardwallet.MainService
import io.moonshard.moonshard.presentation.view.chat.info.tickets.ManageTypesTicketView
import io.moonshard.moonshard.ui.fragments.mychats.chat.info.tickets.buyticket.BuyTicketObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter

@InjectViewState
class ManageTypesTicketPresenter : MvpPresenter<ManageTypesTicketView>() {

    fun getTypesTicket(eventJid:String){
        MainService.getBuyTicketService().getTicketsTypes(eventJid)
           .subscribeOn(Schedulers.io())
           .observeOn(AndroidSchedulers.mainThread())
           .subscribe { typesTicket, throwable ->
               if (throwable == null) {
                   for(i in typesTicket.indices){
                       Log.d("typesTicketId",typesTicket[i].eventId.toString())
                   }
                   viewState.setTypesTicket(typesTicket)
               } else {
                   throwable.message?.let { viewState?.showToast(it) }
               }
           }
    }
}