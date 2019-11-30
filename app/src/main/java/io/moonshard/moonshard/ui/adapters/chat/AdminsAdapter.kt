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
import io.moonshard.moonshard.ui.fragments.chat.AdminsFragment
import org.jivesoftware.smackx.muc.Occupant
import java.util.concurrent.ExecutionException


interface AdminListener {
    fun remove(categoryName: String)
}

class AdminsAdapter(
    val adminsFragment: AdminsFragment,
    val listener: AdminListener,
    private var moderators: List<Occupant>
) :
    RecyclerView.Adapter<AdminsAdapter.ViewHolder>() {

    override fun getItemCount(): Int = moderators.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.admin_item,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.nameTv?.text = moderators[position].nick
        //holder.statusTv?.text = moderators[position].affiliation.
        setAvatar(moderators[position].jid.asUnescapedString(), holder.userAvatar!!)
    }

    fun setAvatar(jid: String, imageView: ImageView) {
        // MainApplication.getXmppConnection().network.loadAvatar("mytest@moonshard.tech")
        var avatarBytes: ByteArray? = ByteArray(0)
        try {
            val future =
                MainApplication.getXmppConnection().network.loadAvatar("mytest@moonshard.tech")

            if (future != null) {
                avatarBytes = future.get()
            }

        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: ExecutionException) {
            e.printStackTrace()
        }

        var avatar: Bitmap? = null
        if (avatarBytes != null) {
            avatar = BitmapFactory.decodeByteArray(avatarBytes, 0, avatarBytes.size)
            imageView.setImageBitmap(avatar)
        }
    }

    fun setAdmins(moderators: List<Occupant>) {
        this.moderators = moderators
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        internal var nameTv: TextView? = view.findViewById(R.id.nameTv)
        internal var statusTv: TextView? = view.findViewById(R.id.statusTv)
        internal var userAvatar: ImageView? = view.findViewById(R.id.userAvatar)
    }

}