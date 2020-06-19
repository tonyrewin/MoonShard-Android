package io.moonshard.moonshard.presentation.presenter.profile.present_ticket

import android.util.Log
import com.example.moonshardwallet.MainService
import com.orhanobut.logger.Logger
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.common.BasePresenter
import io.moonshard.moonshard.common.getLongStringValue
import io.moonshard.moonshard.models.jabber.Recipient
import io.moonshard.moonshard.presentation.view.profile.present_ticket.RecipientDialogiView
import io.moonshard.moonshard.usecase.AuthUseCase
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import org.jivesoftware.smack.roster.RosterEntry
import org.jivesoftware.smackx.vcardtemp.VCardManager
import org.jxmpp.jid.impl.JidCreate
import java.math.BigInteger

@InjectViewState
class RecipientDialogPresenter : BasePresenter<RecipientDialogiView>(){

    private var useCase: AuthUseCase? = null
    private val compositeDisposable = CompositeDisposable()

    private var recipients = ArrayList<Recipient>()
    private var fullRecipients = ArrayList<Recipient>()


    init {
        useCase = AuthUseCase()
    }

    fun getContactsRx() {
        viewState?.showProgressBar()
        Single.create<ArrayList<Recipient>> {

            try {
                val usersSet = MainApplication.getXmppConnection().contactList

                val contacts = ArrayList<RosterEntry>()
                contacts.addAll(usersSet)


                val recipients = ArrayList<Recipient>()

                for (i in contacts.indices) {
                    val jidUser = JidCreate.entityBareFrom(contacts[i].jid)
                    val vm =
                        VCardManager.getInstanceFor(MainApplication.getXmppConnection().connection)
                    val card = vm.loadVCard(jidUser)
                    val nickName: String
                    nickName = if (card.nickName.isNullOrBlank()) {
                        card.to.asBareJid().localpartOrNull.toString()
                    } else {
                        card.nickName
                    }

                    val recipient = Recipient(contacts[i].jid.asUnescapedString(), nickName)
                    recipients.add(recipient)
                }

                it.onSuccess(recipients)
            } catch (e: Exception) {
                it.onError(e)
            }
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                recipients.addAll(it)
                fullRecipients.addAll(it)
                viewState?.hideProgressBar()
                viewState?.showContacts(recipients)
            }, {
                viewState?.hideProgressBar()
                viewState?.showToast("Произошла ошибка")
            })

        // val sequenceInMiliSec = measureTimeMillis {}
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
        viewState?.showProgressBar()
        MainService.getBuyTicketService().presentTicket(walletAddressTo,idTicket)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                viewState?.hideProgressBar()
                viewState?.showToast("Билет отправлен")
                viewState?.back()
                Log.d("ticketAsPresent", "transactionReceipt: " + it.blockHash)
            }, {
                viewState?.hideProgressBar()
                viewState?.dismissBack()
                Logger.d(it)
            })
    }

    fun setFilter(filter: String) {
        Log.d("filterTEst",fullRecipients.size.toString())
        if(filter.isBlank()){
            recipients.clear()
            recipients.addAll(fullRecipients)
            viewState?.onDataChange()
        }else{
            Log.d("filterTEst1",fullRecipients.size.toString())

            val list = fullRecipients.filter {
                it.nickName.contains(filter, true)
            }
            Log.d("filterTEst2",list.size.toString())
            recipients.clear()
            recipients.addAll(list)
            viewState?.onDataChange()
        }
    }
}