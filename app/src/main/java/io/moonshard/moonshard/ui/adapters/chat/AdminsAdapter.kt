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
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jivesoftware.smackx.muc.Occupant
import trikita.log.Log
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
        holder.roleTv?.text = moderators[position].role.name
        setAvatar(moderators[position].jid.asUnescapedString(), holder.userAvatar!!)
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
                        imageView.setImageBitmap(avatar)
                    }
                }, { throwable -> Log.e(throwable.message) })
        }
    }

    fun setAdmins(moderators: List<Occupant>) {
        this.moderators = moderators
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        internal var nameTv: TextView? = view.findViewById(R.id.nameAdminTv)
        internal var roleTv: TextView? = view.findViewById(R.id.roleTv)
        internal var userAvatar: ImageView? = view.findViewById(R.id.userAdminAvatar)
    }

}