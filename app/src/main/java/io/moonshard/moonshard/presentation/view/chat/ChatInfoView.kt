package io.moonshard.moonshard.presentation.view.chat

import com.google.android.gms.maps.model.LatLng
import moxy.MvpView
import org.jivesoftware.smackx.muc.Affiliate

interface ChatInfoView: MvpView {
    fun showMembers(members:List<Affiliate>)
    fun showData(
        name: String,
        occupantsCount: Int,
        onlineMembersValue: Int,
        latLngLocation: LatLng?,
        category: String,
        description: String
    )
}