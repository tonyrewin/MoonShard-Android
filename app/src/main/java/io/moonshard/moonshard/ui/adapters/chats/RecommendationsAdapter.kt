package io.moonshard.moonshard.ui.adapters.chats

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.orhanobut.logger.Logger
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.R
import io.moonshard.moonshard.models.dbEntities.ChatEntity
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jivesoftware.smack.packet.Presence
import org.jivesoftware.smackx.muc.MultiUserChatManager
import org.jivesoftware.smackx.muc.RoomInfo
import org.jxmpp.jid.impl.JidCreate
import trikita.log.Log


interface RecommendationsListener {
    fun recommendationsClick(jid: String)
}

class RecommendationsAdapter(val listener: RecommendationsListener,
                             private var recommendations: List<ChatEntity>) :
    RecyclerView.Adapter<RecommendationsAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getRoomInfo(recommendations[position].jid)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                try {
                    setValueMembersTv(it, recommendations[position].jid, holder.valueMembersTv)
                }catch (e:Exception){
                    Logger.d(e)
                }
            }, {
                Logger.d(it)
            })

        try {
            holder.eventNameTv?.text = recommendations[position].chatName
            setAvatar(recommendations[position].jid, recommendations[position].chatName, holder.avatarEvent)
        }catch (e:Exception){
            Logger.d(e)
        }

        holder.itemView.setOnClickListener {
            listener.recommendationsClick(recommendations[position].jid)
        }
    }

    private fun setAvatar(jid: String, nameChat: String, imageView: ImageView?) {
        if (MainApplication.getCurrentChatActivity() != jid) {
            MainApplication.getXmppConnection().loadAvatar(jid, nameChat)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ bytes ->
                    val avatar: Bitmap?
                    if (bytes != null) {
                        avatar = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                        MainApplication.getMainUIThread().post {
                            imageView?.setImageBitmap(avatar)
                        }
                    }
                }, { throwable ->
                    Log.e(throwable.message)
                })
        }
    }

    fun getRoomInfo(jid: String): Single<RoomInfo> {
        return Single.create {
            val groupId = JidCreate.entityBareFrom(jid)
            val muc =
                MainApplication.getXmppConnection().multiUserChatManager
                    .getMultiUserChat(groupId)
            Logger.d(groupId.asUnescapedString())

            val info =
                MainApplication.getXmppConnection().multiUserChatManager
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
                    MainApplication.getXmppConnection().multiUserChatManager
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

    override fun getItemCount(): Int = recommendations.size


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.recommendation_item,
                parent,
                false
            )
        )

    fun updateRecommendations(newRecommendations:List<ChatEntity>){
        this.recommendations = newRecommendations
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        internal var eventNameTv: TextView? = view.findViewById(R.id.groupNameTv)
        internal var categoryTv: TextView? = view.findViewById(R.id.categoryTv)
        internal var valueMembersTv: TextView? = view.findViewById(R.id.valueMembersTv)
        internal var avatarEvent: ImageView? = view.findViewById(R.id.avatarEvent)
    }
}