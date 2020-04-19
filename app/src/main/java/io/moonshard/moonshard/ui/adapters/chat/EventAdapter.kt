package io.moonshard.moonshard.ui.adapters.chat

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.R
import io.moonshard.moonshard.common.utils.setSafeOnClickListener
import io.moonshard.moonshard.models.api.RoomPin
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jxmpp.jid.impl.JidCreate
import trikita.log.Log


interface EventListener {
    fun eventClick(event: RoomPin)
}

class EventAdapter(
    val listener: EventListener,
    private var events: ArrayList<RoomPin>
) :
    RecyclerView.Adapter<EventAdapter.ViewHolder>() {


    fun update(events: ArrayList<RoomPin>) {
        this.events.clear()
        this.events.addAll(events)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.showGroupBtn?.setSafeOnClickListener {
            listener.eventClick(events[position])
        }
        try {
            val jid = JidCreate.entityBareFrom(events[position].roomId)
            val muc =
                MainApplication.getXmppConnection().multiUserChatManager
                    .getMultiUserChat(jid)

            val roomInfo =
                MainApplication.getXmppConnection().multiUserChatManager
                    .getRoomInfo(jid)
            var context = holder.nameEvent?.getContext()
            holder.nameEvent?.text = roomInfo.name ?: context?.getString(R.string.no_information_available)
            holder.descriptionTv?.text = roomInfo.description ?: context?.getString(R.string.no_information_available)
            setAvatar(events[position].parentGroupId, roomInfo.name, holder.avatarEvent)

        } catch (e: Exception) {
            Log.d(e.message)
        }
    }

    private fun setAvatar(jid: String?, nameChat: String, imageView: ImageView?) {
        if (jid != null) {
            if (MainApplication.getCurrentChatActivity() != jid) {
                MainApplication.getXmppConnection().loadAvatar(jid, nameChat)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ bytes ->
                        val avatar: Bitmap?
                        if (bytes != null) {
                            avatar = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                            imageView?.setImageBitmap(avatar)
                        }
                    }, { throwable -> Log.e(throwable.message) })
            }
        }
    }

    override fun getItemCount(): Int = events.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.event_item,
                parent,
                false
            )
        )

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        internal var nameEvent: TextView? = view.findViewById(R.id.nameEvent)
        internal var descriptionTv: TextView? = view.findViewById(R.id.descriptionTv)
        internal var avatarEvent: ImageView? = view.findViewById(R.id.avatarEvent)
        internal var showGroupBtn: Button? = view.findViewById(R.id.showGroupBtn)
    }
}