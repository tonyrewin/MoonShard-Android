package io.moonshard.moonshard.presentation.presenter.chat.info

import de.adorsys.android.securestoragelibrary.SecurePreferences
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.models.jabber.EventManagerUser
import io.moonshard.moonshard.presentation.view.chat.info.AdminsView
import moxy.InjectViewState
import moxy.MvpPresenter
import org.jxmpp.jid.impl.JidCreate
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList

@InjectViewState
class AdminsPresenter : MvpPresenter<AdminsView>() {

    fun getAdmins(jid: String) {
        viewState?.showProgressBar()
        try {
            val groupId = JidCreate.entityBareFrom(jid)
            val muc =
                MainApplication.getXmppConnection().multiUserChatManager
                    .getMultiUserChat(groupId)
            val moderators = muc.moderators
            val faceControllers = muc.members

            val managers = arrayListOf<EventManagerUser>()

            for (i in moderators.indices) {
                val manager = EventManagerUser(
                    moderators[i].affiliation,
                    moderators[i].jid.asUnescapedString().split("/")[0],
                    moderators[i].nick.toString()
                )
                managers.add(manager)
            }

            for (i in faceControllers.indices) {
                val manager = EventManagerUser(
                    faceControllers[i].affiliation,
                    faceControllers[i].jid.asUnescapedString(),
                    ""
                )
                managers.add(manager)
            }

            viewState?.showAdmins(removeDuplicates(managers))
            viewState?.hideProgressBar()
        } catch (e: Exception) {
            viewState?.hideProgressBar()
            e.message?.let { viewState?.showToast(it) }
        }
    }

    private fun removeDuplicates(list: List<EventManagerUser>?): ArrayList<EventManagerUser> {
        val s: MutableSet<EventManagerUser> = TreeSet<EventManagerUser>(Comparator<EventManagerUser?> { o1, o2 ->
            if(o1!!.jid==o2!!.jid){
                0
            }else{
                1
            }
            })
        s.addAll(list!!)

        val newArray = arrayListOf<EventManagerUser>()
        newArray.addAll(s)
        return newArray
    }


}