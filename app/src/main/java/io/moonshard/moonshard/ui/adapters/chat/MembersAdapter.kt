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
import org.jivesoftware.smackx.muc.Affiliate
import java.util.concurrent.ExecutionException


interface MemberListener {
    fun remove(categoryName: String)
}

class MembersAdapter(
    val listener: MemberListener,
    private var members: List<Affiliate>
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
        holder.nameTv?.text = members[position].nick
        //holder.statusTv?.text = members[position].affiliation.
        setAvatar(members[position].jid.asUnescapedString(), holder.userAvatar!!)
    }

    private fun setAvatar(jid: String, imageView: ImageView) {
        var avatarBytes: ByteArray? = ByteArray(0)
        try {
            val future =
                MainApplication.getXmppConnection().network.loadAvatar(jid)

            if (future != null) {
                avatarBytes = future.get()
            }

        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: ExecutionException) {
            e.printStackTrace()
        }

        val avatar: Bitmap?
        if (avatarBytes != null) {
            avatar = BitmapFactory.decodeByteArray(avatarBytes, 0, avatarBytes.size)
            imageView.setImageBitmap(avatar)
        }
    }

    fun setMembers(moderators: List<Affiliate>) {
        this.members = moderators
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var nameTv: TextView? = view.findViewById(R.id.nameMemberTv)
        var statusTv: TextView? = view.findViewById(R.id.statusMemberTv)
        var userAvatar: ImageView? = view.findViewById(R.id.userMemberAvatar)
    }
}