package io.moonshard.moonshard.presentation.view.chat

import android.graphics.Bitmap
import com.google.android.gms.maps.model.LatLng
import moxy.MvpView
import org.jivesoftware.smackx.muc.Affiliate

interface ChatInfoView: MvpView {
    fun showMembers(members:List<Affiliate>)
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