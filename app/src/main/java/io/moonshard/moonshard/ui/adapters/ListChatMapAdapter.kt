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
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
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
        try {
            getRoomInfo(chats[position].roomId.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    setValueMembersTv(it, chats[position].roomId.toString(), holder.valueMembersTv)
                    setAvatar(chats[position].roomId.toString(), holder.groupIv!!, it.name!!)
                    holder.groupNameTv?.text = it.name.toString()
                }, {
                    Logger.d(it)
                })

            calculationByDistance(chats[position].latitude.toString(), chats[position].longitude.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    holder.locationValueTv?.text = it
                }, {
                    Logger.d(it)
                })

            holder.itemView.setSafeOnClickListener {
                listener.clickChat(chats[position])
            }
        } catch (e: Exception) {
            Logger.d(e)
        }
    }

    fun getRoomInfo(jid: String): Single<RoomInfo> {
        return Single.create {
            val groupId = JidCreate.entityBareFrom(jid)
            val muc =
                MultiUserChatManager.getInstanceFor(MainApplication.getXmppConnection().connection)
                    .getMultiUserChat(groupId)

            val info =
                MultiUserChatManager.getInstanceFor(MainApplication.getXmppConnection().connection)
                    .getRoomInfo(muc.room)

            it.onSuccess(info)
        }
    }

    private fun setValueMembersTv(roomInfo: RoomInfo, jid: String, valueMembersTv: TextView?) {
        getValueOnlineUsers(jid).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                valueMembersTv?.text = "${roomInfo.occupantsCount} человек, $it онлайн"
            }, {

            })
    }

    private fun getValueOnlineUsers(jid: String): Single<Int> {
        return Single.create {
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
                it.onSuccess(onlineValue)
            } catch (e: Exception) {
                it.onSuccess(0)
            }
        }
    }

    private fun calculationByDistance(latRoom: String?, lngRoom: String?): Single<String> {
        return Single.create {
            if (latRoom != null && lngRoom != null) {

                MainApplication.getCurrentLocation()?.let { location ->
                    val myLat = MainApplication.getCurrentLocation().latitude
                    val myLng = MainApplication.getCurrentLocation().longitude

                    val km = SphericalUtil.computeDistanceBetween(
                        LatLng(latRoom.toDouble(), lngRoom.toDouble()),
                        LatLng(myLat, myLng)
                    ).toInt() / 1000
                    if (km < 1) {
                        it.onSuccess(
                            SphericalUtil.computeDistanceBetween(
                                LatLng(
                                    latRoom.toDouble(),
                                    lngRoom.toDouble()
                                ), LatLng(myLat, myLng)
                            ).toInt().toString() + " метрах"
                        )
                    } else {
                        it.onSuccess(
                            (SphericalUtil.computeDistanceBetween(
                                LatLng(latRoom.toDouble(), lngRoom.toDouble()),
                                LatLng(myLat, myLng)
                            ).toInt() / 1000).toString() + " км"
                        )
                    }
                }
            }
            it.onSuccess("")
        }
    }

    private fun setAvatar(jid: String, imageView: ImageView, nameChat: String) {
        if (MainApplication.getCurrentChatActivity() != jid) {
            MainApplication.getXmppConnection().loadAvatar(jid, nameChat)
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

    fun setChats(newChats: ArrayList<RoomPin>) {
        chats.clear()
        chats.addAll(newChats)
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