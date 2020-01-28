package io.moonshard.moonshard.ui.adapters.create

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.hdodenhof.circleimageview.CircleImageView
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.R
import io.moonshard.moonshard.common.utils.setSafeOnClickListener
import io.moonshard.moonshard.db.ChooseChatRepository
import io.moonshard.moonshard.models.dbEntities.ChatEntity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import trikita.log.Log


interface GroupsListener {
    fun clickChat(categoryName: ChatEntity)
}

class GroupsAdapter(val listener: GroupsListener, private var groups: ArrayList<ChatEntity>) :
    RecyclerView.Adapter<GroupsAdapter.ViewHolder>() {

    var focusedItem = -1
    private fun setFocusedItem() {
        for (i in groups.indices) {
            if (groups[i].jid == ChooseChatRepository.group?.jid) {
                focusedItem = i
            }
        }
    }

    fun updateGroups(groups: ArrayList<ChatEntity>) {
        this.groups.clear()
        this.groups.addAll(groups)
        setFocusedItem()
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.category_item,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (focusedItem == position) {
            holder.categoryName?.setTextColor(Color.parseColor("#0075FF"))
            holder.mainLayout?.setBackgroundColor(Color.parseColor("#EEF6FF"))
        } else {
            holder.mainLayout?.setBackgroundColor(Color.parseColor("#FFFFFF"))
            holder.categoryName?.setTextColor(Color.parseColor("#333333"))
        }

        holder.iconCategory?.let {
            setAvatar(groups[position].jid, groups[position].chatName, it)
        }

        holder.categoryName?.text = groups[position].chatName

        holder.itemView.setSafeOnClickListener {
            focusedItem = position
            notifyDataSetChanged()
            listener.clickChat(groups[position])
        }
    }

    private fun setAvatar(jid: String, nameChat: String, imageView: ImageView) {
        if (MainApplication.getCurrentChatActivity() != jid) {
            MainApplication.getXmppConnection().loadAvatar(jid, nameChat)
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

    override fun getItemCount(): Int = groups.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        internal var categoryName: TextView? = view.findViewById(R.id.name)
        internal var iconCategory: CircleImageView? = view.findViewById(R.id.iconCategory)
        internal var mainLayout: LinearLayout? = view.findViewById(R.id.mainLayout)
    }
}