package io.moonshard.moonshard.presentation.view.chat.info

import android.graphics.Bitmap
import com.google.android.gms.maps.model.LatLng
import moxy.MvpView
import org.jivesoftware.smackx.muc.Occupant

interface EventInfoView: MvpView {
    fun showMembers(members: List<Occupant>)
    fun showData(
        name: String,
        occupantsCount: Int,
        onlineMembersValue: Int,
        latLngLocation: LatLng?,
        category: String,
        description: String,
        address: String?
    )

    fun showError(error: String)
    fun showChatsScreen()
    fun setAvatar(avatar: Bitmap?)
    fun setAvatarOrganizer(avatar: Bitmap?)
    fun hideOrganizerLayout()
    fun showDataOrganizer(organizerName:String)

    fun showChangeChatButton(isShow: Boolean, type: String?)
    fun hideLine()
    fun hideDescription()
    fun setStartDate(date:String)
    fun showProgressBar()
    fun hideProgressBar()
}