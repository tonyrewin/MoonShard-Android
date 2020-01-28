package io.moonshard.moonshard.ui.adapters

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import com.orhanobut.logger.Logger
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.R
import io.moonshard.moonshard.common.utils.setSafeOnClickListener
import io.moonshard.moonshard.models.api.RoomPin
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jivesoftware.smack.packet.Presence
import org.jivesoftware.smackx.muc.MultiUserChatManager
import org.jivesoftware.smackx.muc.RoomInfo
import org.jxmpp.jid.impl.JidCreate
import trikita.log.Log

//todo need add presenter
interface ListChatMapListener {
    fun clickChat(room: RoomPin)
}

class ListChatMapAdapter(val listener: ListChatMapListener, private var chats: ArrayList<RoomPin>) :
    RecyclerView.Adapter<ListChatMapAdapter.ViewHolder>() {

    var focusedItem = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.chat_map_item,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val roomInfo = getRoom(chats[position].roomId.toString())
        val onlineUser = getValueOnlineUsers(
            chats[position].roomId.toString())

        setAvatar(chats[position].roomId.toString(), holder.groupIv!!)
        holder.groupNameTv?.text = roomInfo?.name.toString()
        holder.valueMembersTv?.text = "${roomInfo?.occupantsCount} человек, $onlineUser онлайн"
        holder.locationValueTv?.text =
            calculationByDistance(chats[position].latitude, chats[position].longitude)

        holder.itemView.setSafeOnClickListener {
            listener.clickChat(chats[position])
        }
    }

    fun getRoom(jid: String): RoomInfo? {
        try {
            val groupId = JidCreate.entityBareFrom(jid)
            val muc =
                MultiUserChatManager.getInstanceFor(MainApplication.getXmppConnection().connection)
                    .getMultiUserChat(groupId)
            Logger.d(groupId.asUnescapedString())

            val info =
                MultiUserChatManager.getInstanceFor(MainApplication.getXmppConnection().connection)
                    .getRoomInfo(muc.room)
            return info
        } catch (e: Exception) {
            val error = ""
            Logger.d(e.message)
            Logger.d(jid)
        }
        return null
    }

    fun getValueOnlineUsers(jid: String): Int {
        try {
            val groupId = JidCreate.entityBareFrom(jid)

            val muc =
                MultiUserChatManager.getInstanceFor(MainApplication.getXmppConnection().connection)
                    .getMultiUserChat(groupId)
            val members = muc.occupants

            var onlineValue = 0
            for (i in members.indices) {
                val userOccupantPresence =
                    muc.getOccupantPresence(members[i].asEntityFullJidIfPossible())
                if (userOccupantPresence.type == Presence.Type.available) {
                    onlineValue++
                }
            }
            return onlineValue
        }catch (e:Exception){
            return 0
        }
    }

    private fun calculationByDistance(latRoom: String?, lngRoom: String?): String {
        if(latRoom!=null && lngRoom!=null) {

            MainApplication.getCurrentLocation()?.let {
                val myLat = MainApplication.getCurrentLocation().latitude
                val myLng = MainApplication.getCurrentLocation().longitude

                val km = SphericalUtil.computeDistanceBetween(
                    LatLng(latRoom.toDouble(), lngRoom.toDouble()),
                    LatLng(myLat, myLng)
                ).toInt() / 1000
                return if (km < 1) {
                    (SphericalUtil.computeDistanceBetween(
                        LatLng(
                            latRoom.toDouble(),
                            lngRoom.toDouble()
                        ), LatLng(myLat, myLng)
                    ).toInt()).toString() + " метрах"
                } else {
                    (SphericalUtil.computeDistanceBetween(
                        LatLng(latRoom.toDouble(), lngRoom.toDouble()),
                        LatLng(myLat, myLng)
                    ).toInt() / 1000).toString() + " км"
                }
            }
        }
        return ""
    }

    private fun setAvatar(jid: String, imageView: ImageView) {
        if (MainApplication.getCurrentChatActivity() != jid) {
            MainApplication.getXmppConnection().loadAvatar(jid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ bytes ->
                    val avatar: Bitmap?
                    if (bytes != null) {
                        avatar = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                        MainApplication.getMainUIThread().post {
                            imageView.setImageBitmap(avatar)
                        }
                    }
                }, { throwable -> Log.e(throwable.message) })
        }
    }

    fun setChats(moderators: ArrayList<RoomPin>) {
        chats.clear()
        chats.addAll(moderators)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = chats.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        internal var groupNameTv: TextView? = view.findViewById(R.id.groupNameTv)
        internal var valueMembersTv: TextView? = view.findViewById(R.id.valueMembersTv)
        internal var locationValueTv: TextView? = view.findViewById(R.id.locationValueTv)
        internal var groupIv: ImageView? = view.findViewById(R.id.profileImage)
        internal var mainLayout: RelativeLayout? = view.findViewById(R.id.mainLayout)
    }
}