package io.moonshard.moonshard.presentation.presenter.profile.wallet.transfer

import com.orhanobut.logger.Logger
import io.moonshard.moonshard.MainApplication
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

    fun getContacts() {

        //todo плашка октрывается дольше из-за этого кода
        try {
            val usersSet = MainApplication.getXmppConnection().contactList

            val contacts = ArrayList<RosterEntry>()
            contacts.addAll(usersSet)


            val recipients = ArrayList<Recipient>()

            for (i in contacts.indices) {
                val jidUser = JidCreate.entityBareFrom(contacts[i].jid)
                val vm = VCardManager.getInstanceFor(MainApplication.getXmppConnection().connection)
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

            viewState?.showContacts(recipients)
        } catch (e: Exception) {
            Logger.d(e)
        }
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
                viewState?.hideProgressBar()
                viewState?.showContacts(it)
            }, {
                viewState?.hideProgressBar()
                viewState?.showToast("Произошла ошибка")
                 Logger.d(it)
            })

        // val sequenceInMiliSec = measureTimeMillis {}
    }
}