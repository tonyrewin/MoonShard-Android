package io.moonshard.moonshard.presentation.presenter.profile.wallet.transfer

import android.util.Log
import com.orhanobut.logger.Logger
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.models.dbEntities.ChatEntity
import io.moonshard.moonshard.models.jabber.Recipient
import io.moonshard.moonshard.presentation.view.profile.wallet.transfer.TransferRecipientDialogView
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import org.jivesoftware.smack.roster.RosterEntry
import org.jivesoftware.smackx.vcardtemp.VCardManager
import org.jxmpp.jid.impl.JidCreate

@InjectViewState
class TransferRecipientDialogPresenter : MvpPresenter<TransferRecipientDialogView>() {

    private var recipients = ArrayList<Recipient>()
    private var fullRecipients = ArrayList<Recipient>()

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
                Log.d("filterTEst","repeat" + fullRecipients.size.toString())
                recipients.addAll(it)
                fullRecipients.addAll(it)
                viewState?.hideProgressBar()
                viewState?.showContacts(recipients)
            }, {
                viewState?.hideProgressBar()
                viewState?.showToast("Произошла ошибка")
                 Logger.d(it)
            })

        // val sequenceInMiliSec = measureTimeMillis {}
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