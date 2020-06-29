package io.moonshard.moonshard.presentation.view.chat.info

import io.moonshard.moonshard.models.jabber.EventManagerUser
import moxy.MvpView
import org.jivesoftware.smackx.muc.Affiliate
import org.jivesoftware.smackx.muc.Occupant

interface AdminsView: MvpView {
    fun showToast(text:String)
    fun showAdmins(managers: ArrayList<EventManagerUser>)
    fun showProgressBar()
    fun hideProgressBar()
    fun showAdminPermission(manager: EventManagerUser)
}