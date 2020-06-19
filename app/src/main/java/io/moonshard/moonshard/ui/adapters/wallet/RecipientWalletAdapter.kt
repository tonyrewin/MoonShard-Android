package io.moonshard.moonshard.ui.adapters.wallet

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.orhanobut.logger.Logger
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.R
import io.moonshard.moonshard.models.jabber.Recipient
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jivesoftware.smack.roster.RosterEntry
import org.jivesoftware.smackx.vcardtemp.VCardManager
import org.jxmpp.jid.impl.JidCreate
import trikita.log.Log


interface RecipientWalletListener {
    fun click(asUnescapedString: String)
}

class RecipientWalletAdapter(
    val listener: RecipientWalletListener,
    private var contacts: ArrayList<Recipient>
) :
    RecyclerView.Adapter<RecipientWalletAdapter.ViewHolder>() {

    var selectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.recipient_item,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.checkBox.setOnCheckedChangeListener(null)

        holder.checkBox.isChecked = holder.adapterPosition==selectedPosition

        holder.checkBox.setOnCheckedChangeListener { view, isChecked ->
            listener.click(contacts[holder.adapterPosition].jid)
            selectedPosition = holder.adapterPosition
            notifyDataSetChanged()
        }

        holder.nameTv?.text = contacts[holder.adapterPosition].nickName
        setAvatar(contacts[position].jid, contacts[holder.adapterPosition].nickName, holder.avatar)

        if (position == contacts.size - 1) {
            holder.viewLine?.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = contacts.size

    fun setContacts(contacts: ArrayList<Recipient>) {
        this.contacts = contacts
        notifyDataSetChanged()
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

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        internal var checkBox: CheckBox = view.findViewById(R.id.checkBox)
        internal var avatar: ImageView? = view.findViewById(R.id.contactAvatar)
        internal var statusTv: TextView? = view.findViewById(R.id.statusTv)
        internal var nameTv: TextView? = view.findViewById(R.id.nameTv)
        internal var viewLine: View? = view.findViewById(R.id.viewLine)
    }
}