package io.moonshard.moonshard.presentation.view.chat

import moxy.MvpView
import org.jivesoftware.smackx.muc.Affiliate
import org.jivesoftware.smackx.muc.Occupant

interface MembersChatView: MvpView {
    fun showError(error:String)
    fun showMembers(members:List<Affiliate>)
}