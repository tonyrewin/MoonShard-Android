package io.moonshard.moonshard.presentation.view.chat

import moxy.MvpView
import org.jivesoftware.smackx.muc.Affiliate
import org.jivesoftware.smackx.muc.Occupant
import org.jxmpp.jid.EntityFullJid

interface MembersChatView: MvpView {
    fun showError(error:String)
    fun showMembers(members:List<Occupant>)
    fun removeMember(member: Occupant)
}