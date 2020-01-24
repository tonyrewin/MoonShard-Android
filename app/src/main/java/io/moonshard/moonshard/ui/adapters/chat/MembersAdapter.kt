package io.moonshard.moonshard.ui.adapters.chat

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.adorsys.android.securestoragelibrary.SecurePreferences
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jivesoftware.smackx.muc.Occupant
import org.jxmpp.jid.EntityFullJid
import trikita.log.Log
import java.lang.Exception


interface MemberListener {
    fun remove(member: Occupant)
    fun clickMember(member: String)
}

class MembersAdapter(
    val listener: MemberListener,
    private var members: ArrayList<Occupant>, var isRemove: Boolean
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
        if (isRemove) {
            holder.removeBtn?.visibility = View.VISIBLE
        } else {
            holder.removeBtn?.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            listener.clickMember(members[position].jid.asBareJid().asUnescapedString())
        }

        holder.removeBtn?.setOnClickListener {
            listener.remove(members[position])
        }

        holder.nameTv?.text = members[position].nick
        //holder.statusTv?.text = members[position].affiliation.
        try {
            setAvatar(members[position].jid.asBareJid().asUnescapedString(), holder.userAvatar!!
            )
        }catch (e:Exception){

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
                        MainApplication.getMainUIThread().post {
                            imageView.setImageBitmap(avatar)
                        }
                    }
                }, { throwable -> Log.e(throwable.message) })
        }
    }

    fun setMembers(members: List<Occupant>) {
        this.members.clear()
        this.members.addAll(members)
        notifyDataSetChanged()
    }

    fun removeMember(member: Occupant){
        this.members.remove(member)
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var nameTv: TextView? = view.findViewById(R.id.nameMemberTv)
        var statusTv: TextView? = view.findViewById(R.id.statusMemberTv)
        var userAvatar: ImageView? = view.findViewById(R.id.userMemberAvatar)
        var removeBtn: ImageView? = view.findViewById(R.id.removeBtn)
    }
}