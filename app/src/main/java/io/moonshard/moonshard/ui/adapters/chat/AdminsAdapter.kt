package io.moonshard.moonshard.ui.adapters.chat

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.R
import io.moonshard.moonshard.common.utils.setSafeOnClickListener
import io.moonshard.moonshard.models.jabber.EventManagerUser
import io.moonshard.moonshard.ui.activities.onboardregistration.VCardCustomManager
import io.moonshard.moonshard.ui.fragments.mychats.chat.info.AdminsFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jivesoftware.smackx.muc.MUCAffiliation
import org.jivesoftware.smackx.muc.Occupant
import org.jxmpp.jid.impl.JidCreate
import trikita.log.Log


interface AdminListener {
    fun remove(categoryName: String)
    fun clickAdminPermission(occupant: EventManagerUser)
}

class AdminsAdapter(
    val listener: AdminListener,
    private var managers: ArrayList<EventManagerUser>
) :
    RecyclerView.Adapter<AdminsAdapter.ViewHolder>() {

    override fun getItemCount(): Int = managers.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.admin_item,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(managers[position].nickName.isBlank()){
            val muc = MainApplication.getXmppConnection().multiUserChatManager
                .getMultiUserChat(JidCreate.entityBareFrom(managers[position].jid))
            val vm =
                VCardCustomManager.getInstanceFor(MainApplication.getXmppConnection().connection)
            val card = vm.loadVCardMuc(muc.room)
            holder.nameTv?.text =card.nickName
        }else{
            holder.nameTv?.text = managers[position].nickName
        }

        when (managers[position].roleType) {
            MUCAffiliation.owner -> {
                holder.roleTv?.text = "Создатель"
            }
            MUCAffiliation.admin -> {
                holder.roleTv?.text = "Администратор"
            }
            MUCAffiliation.member -> {
                holder.roleTv?.text = "Фейс контрольщик"
            }
        }

        setAvatar(managers[position].jid, holder.userAvatar!!)

        holder.itemView.setSafeOnClickListener {
            listener.clickAdminPermission(managers[position])
        }
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
                        imageView.setImageBitmap(avatar)
                    }
                }, { throwable -> Log.e(throwable.message) })
        }
    }

    fun setAdmins(managers: ArrayList<EventManagerUser>) {
        this.managers.clear()
        this.managers.addAll(managers)
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        internal var nameTv: TextView? = view.findViewById(R.id.nameAdminTv)
        internal var roleTv: TextView? = view.findViewById(R.id.roleTv)
        internal var userAvatar: ImageView? = view.findViewById(R.id.userAdminAvatar)
    }
}