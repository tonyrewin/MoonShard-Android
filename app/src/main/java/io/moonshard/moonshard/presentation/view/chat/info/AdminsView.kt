package io.moonshard.moonshard.presentation.view.chat.info

import moxy.MvpView
import org.jivesoftware.smackx.muc.Affiliate
import org.jivesoftware.smackx.muc.Occupant

interface AdminsView: MvpView {
    fun showToast(text:String)
    fun showAdmins(admins:List<Occupant>)
}