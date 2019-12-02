package io.moonshard.moonshard.presentation.view.chat

import android.graphics.Bitmap
import com.google.android.gms.maps.model.LatLng
import moxy.MvpView
import org.jivesoftware.smackx.muc.Affiliate
import org.jivesoftware.smackx.muc.Occupant
import org.jxmpp.jid.EntityFullJid

interface ChatInfoView: MvpView {
    fun showMembers(members:List<EntityFullJid>)
    fun showData(avatar:Bitmap?,
        name: String,
        occupantsCount: Int,
        onlineMembersValue: Int,
        latLngLocation: LatLng?,
        category: String,
        description: String
    )
    fun showError(error:String)
    fun showChatsScreen()
}