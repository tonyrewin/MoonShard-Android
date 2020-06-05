package io.moonshard.moonshard.presentation.presenter.profile.present_ticket

import android.util.Log
import com.example.moonshardwallet.MainService
import com.orhanobut.logger.Logger
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.common.BasePresenter
import io.moonshard.moonshard.presentation.view.profile.present_ticket.RecipientDialogiView
import moxy.InjectViewState
import org.jivesoftware.smack.roster.RosterEntry
import java.math.BigInteger

@InjectViewState
class RecipientDialogPresenter : BasePresenter<RecipientDialogiView>(){

    fun getContacts() {
        try {
            val usersSet = MainApplication.getXmppConnection().contactList

            val contacts = ArrayList<RosterEntry>()
            contacts.addAll(usersSet)

            viewState?.showContacts(contacts)
        }catch (e:Exception){
            Logger.d(e)
        }
    }

    fun sendTicketAsPresent(walletAddressTo:String,idTicket:BigInteger){
        MainService.getBuyTicketService().presentTicketRx(
            walletAddressTo,
            idTicket
        ).thenAccept {
            viewState?.back()
        }.exceptionally { e ->
            viewState?.dismissBack()
            null
        }
    }
}