package io.moonshard.moonshard.ui.adapters.wallet

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Image
import android.os.Handler
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
import io.moonshard.moonshard.models.RosterEntryCustom
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.internal.http2.Http2Reader
import org.jivesoftware.smack.roster.RosterEntry
import org.jivesoftware.smackx.vcardtemp.VCardManager
import org.jxmpp.jid.impl.JidCreate
import trikita.log.Log


interface RecipientWalletListener {
    fun click()
}

class RecipientWalletAdapter(val listener: RecipientWalletListener, private var contacts: ArrayList<RosterEntry>) :
    RecyclerView.Adapter<RecipientWalletAdapter.ViewHolder>()  {

    var selectedPosition = -1
    var lastItem = -1

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

        holder.checkBox.isChecked = selectedPosition == position

        holder.checkBox.setOnCheckedChangeListener { view, isChecked ->
                selectedPosition = position
                notifyDataSetChanged()
        }

        holder.checkBox.setOnClickListener {
            selectedPosition = position
            notifyDataSetChanged()
        }

        try {
            val jidUser = JidCreate.entityBareFrom(contacts[position].jid)
            val vm = VCardManager.getInstanceFor(MainApplication.getXmppConnection().connection)
            val card = vm.loadVCard(jidUser)
            val nickName: String
            nickName = if(card.nickName.isNullOrBlank()){
                card.to.asBareJid().localpartOrNull.toString()
            }else{
                card.nickName
            }

            holder.nameTv?.text = nickName
            setAvatar(contacts[position].jid!!.asUnescapedString(),nickName,holder.avatar)
        }catch (e:Exception){
            Logger.d(e)
        }

        if(position==contacts.size-1){
            holder.viewLine?.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = contacts.size

    fun setContacts(contacts: ArrayList<RosterEntry>) {
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
        internal var checkBox:CheckBox = view.findViewById(R.id.checkBox)
        internal var avatar: ImageView? = view.findViewById(R.id.contactAvatar)
        internal var statusTv: TextView? = view.findViewById(R.id.statusTv)
        internal var nameTv: TextView? = view.findViewById(R.id.nameTv)
        internal var viewLine: View? = view.findViewById(R.id.viewLine)
    }
}