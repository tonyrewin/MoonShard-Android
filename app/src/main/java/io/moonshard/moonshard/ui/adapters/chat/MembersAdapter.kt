package io.moonshard.moonshard.ui.adapters.chat

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.orhanobut.logger.Logger
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.R
import io.moonshard.moonshard.common.utils.setSafeOnClickListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jivesoftware.smackx.muc.MUCAffiliation
import org.jivesoftware.smackx.muc.Occupant
import trikita.log.Log


interface MemberListener {
    fun remove(member: Occupant)
    fun clickMember(member: String)
}

class MembersAdapter(
    val listener: MemberListener,
    private var members: ArrayList<Occupant>,
    var isRemove: Boolean,
    var myTypeRole: String?
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
            when (members[position].affiliation) {
                MUCAffiliation.owner -> {
                    holder.statusTv?.text = "Создатель"
                    holder.removeBtn?.visibility = View.GONE
                }
                MUCAffiliation.admin -> {
                    if (myTypeRole == "owner") {
                        holder.removeBtn?.visibility = View.VISIBLE
                    } else if (myTypeRole == "admin") {
                        holder.removeBtn?.visibility = View.GONE
                    }
                    holder.statusTv?.text = "Администратор"
                }
                MUCAffiliation.member -> {
                    if (myTypeRole == "owner") {
                        holder.removeBtn?.visibility = View.VISIBLE
                    } else if (myTypeRole == "admin") {
                        holder.removeBtn?.visibility = View.GONE
                    }
                    holder.statusTv?.text = "Фейс-контрольщик"
                }
                else -> {
                    holder.removeBtn?.visibility = View.VISIBLE
                    holder.statusTv?.text = "Участник"
                }
            }
        } else {
            holder.removeBtn?.visibility = View.GONE

            when (members[position].affiliation) {
                MUCAffiliation.owner -> {
                    holder.roleTv?.text = "Создатель"
                }
                MUCAffiliation.admin -> {
                    holder.roleTv?.text = "Администратор"
                }
                MUCAffiliation.member -> {
                    holder.roleTv?.text = "Фейс-контрольщик"
                }
                else -> {
                    holder.roleTv?.text = "Участник"
                }
            }
        }

        holder.itemView.setSafeOnClickListener {
            listener.clickMember(members[position].jid.asBareJid().asUnescapedString())
        }

        holder.removeBtn?.setSafeOnClickListener {
            listener.remove(members[position])
        }

        holder.nameTv?.text = members[position].nick

        try {
            setAvatar(members[position].jid.asBareJid().asUnescapedString(), holder.userAvatar!!)
        } catch (e: Exception) {
            Logger.d(e)
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

    fun removeMember(member: Occupant) {
        this.members.remove(member)
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var nameTv: TextView? = view.findViewById(R.id.nameMemberTv)
        var statusTv: TextView? = view.findViewById(R.id.statusMemberTv)
        var roleTv: TextView? = view.findViewById(R.id.roleTv)
        var userAvatar: ImageView? = view.findViewById(R.id.userMemberAvatar)
        var removeBtn: ImageView? = view.findViewById(R.id.removeBtn)
    }
}