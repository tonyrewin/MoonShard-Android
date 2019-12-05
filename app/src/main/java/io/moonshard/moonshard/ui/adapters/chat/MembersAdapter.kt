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
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jxmpp.jid.EntityFullJid
import trikita.log.Log


interface MemberListener {
    fun remove(categoryName: String)
}

class MembersAdapter(
    val listener: MemberListener,
    private var members: List<EntityFullJid>
) :
    RecyclerView.Adapter<MembersAdapter.ViewHolder>() {

    override fun getItemCount(): Int = members.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.member_item,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.nameTv?.text = members[position].resourceOrEmpty
        //holder.statusTv?.text = members[position].affiliation.
        setAvatar(
            members[position].resourceOrEmpty.toString() + "@moonshard.tech",
            holder.userAvatar!!
        )
    }

    private fun setAvatar(jid: String, imageView: ImageView) {
        if (MainApplication.getCurrentChatActivity() != jid) {
            MainApplication.getXmppConnection().loadAvatar(jid)
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ bytes ->
                    val avatar: Bitmap?
                    if (bytes != null) {
                        avatar = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                        MainApplication.getMainUIThread().post{
                            imageView.setImageBitmap(avatar)
                        }                    }
                }, { throwable -> Log.e(throwable.message) })
        }
    }

    fun setMembers(members: List<EntityFullJid>) {
        this.members = members
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var nameTv: TextView? = view.findViewById(R.id.nameMemberTv)
        var statusTv: TextView? = view.findViewById(R.id.statusMemberTv)
        var userAvatar: ImageView? = view.findViewById(R.id.userMemberAvatar)
    }
}