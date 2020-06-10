package io.moonshard.moonshard.presentation.presenter.profile.present_ticket

import android.util.Log
import com.example.moonshardwallet.MainService
import com.orhanobut.logger.Logger
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.common.BasePresenter
import io.moonshard.moonshard.common.getLongStringValue
import io.moonshard.moonshard.presentation.view.profile.present_ticket.RecipientDialogiView
import io.moonshard.moonshard.usecase.AuthUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import org.jivesoftware.smack.roster.RosterEntry
import java.math.BigInteger

@InjectViewState
class RecipientDialogPresenter : BasePresenter<RecipientDialogiView>(){

    private var useCase: AuthUseCase? = null
    private val compositeDisposable = CompositeDisposable()


    init {
        useCase = AuthUseCase()
    }


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

    fun sendTicketAsPresent(jid:String?,idTicket:BigInteger){
        getWalletAddress(jid,idTicket)
    }

    private fun getWalletAddress(jid: String?, idTicket: BigInteger) {
        if(!jid.isNullOrBlank()) {
            val accessToken = getLongStringValue("accessToken")
            Log.d("ticketAsPresent", "jid: $jid")
            Log.d("ticketAsPresent", "idTicket: $idTicket")

            compositeDisposable.add(useCase!!.getWalletAddress(
                jid, accessToken!!
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { result, throwable ->
                    if (throwable == null) {
                        sendTicket(result.walletAddress,idTicket)
                    } else {
                        Logger.d(result)
                    }
                })
        }
    }

    fun sendTicket(walletAddressTo:String,idTicket:BigInteger){
        MainService.getBuyTicketService().presentTicket(walletAddressTo,idTicket)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                viewState?.back()
                Log.d("ticketAsPresent", "transactionReceipt: " + it.blockHash)
            }, {
                viewState?.dismissBack()
                Logger.d(it)
            })
    }
}