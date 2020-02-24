package io.moonshard.moonshard.ui.adapters.chat

import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.CornerFamily
import com.squareup.picasso.Picasso
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.R
import io.moonshard.moonshard.models.GenericMessage
import io.moonshard.moonshard.ui.activities.RecyclerScrollMoreListener
import io.moonshard.moonshard.ui.adapters.DateFormatter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import trikita.log.Log
import java.util.*

interface PhotoListener {
    fun clickPhoto(url:String)
}

open class MessagesAdapter(
    private var myMsgs: MutableList<GenericMessage>,
    val layoutManager: LinearLayoutManager,
    val listener:PhotoListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), RecyclerScrollMoreListener.OnLoadMoreListener {

    private var loadMoreListener: OnLoadMoreListener? = null

         var isImageFitToScreen:Boolean = false


    fun setLoadMoreListener(loadMoreListener: OnLoadMoreListener) {
        this.loadMoreListener = loadMoreListener
    }

    override fun onLoadMore(page: Int, total: Int) {
        if (loadMoreListener != null) {
            loadMoreListener!!.onLoadMore(page, total)
        }
    }

    override fun getMessagesCount(): Int {
        var count = 0
        for (item in myMsgs) {
            count++
        }
        return count
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            0 -> {
                return ViewHolderMyMessage(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.my_message,
                        parent,
                        false
                    )
                )
            }
            1 -> {
                return ViewHolderDifferentMessage(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.their_message,
                        parent,
                        false
                    )
                )
            }
            2 -> {
                return ViewHolderDifferentMessage(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.system_message,
                        parent,
                        false
                    )
                )
            }
            else -> {
                return ViewHolderMyMessage(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.my_message,
                        parent,
                        false
                    )
                )
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (myMsgs[position].isSystemMessage) {
            2
        } else {
            if (myMsgs[position].isBelongsToCurrentUser) {
                0
            } else {
                1
            }
        }
    }

    @SuppressLint("UnsafeExperimentalUsageError")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            0 -> {
                (holder as ViewHolderMyMessage).mainImage?.setOnClickListener {
                    listener.clickPhoto(myMsgs[position].text)
                }

                holder.mainImage?.setShapeAppearanceModel(
                    holder.mainImage?.shapeAppearanceModel
                        ?.toBuilder()
                        ?.setTopRightCorner(CornerFamily.ROUNDED, (16 * Resources.getSystem().displayMetrics.density))
                        ?.setTopLeftCorner(CornerFamily.ROUNDED, (16 * Resources.getSystem().displayMetrics.density))
                        ?.setBottomRightCorner(CornerFamily.ROUNDED, (2 * Resources.getSystem().displayMetrics.density))
                        ?.setBottomLeftCorner(CornerFamily.ROUNDED, (16 * Resources.getSystem().displayMetrics.density))
                    !!.build())

                if (myMsgs[position].isFile) {

                    if(myMsgs[position].isImage){
                        holder.layoutFile?.visibility = View.GONE
                        holder.layoutMessage?.visibility = View.GONE
                        holder.mainImage?.visibility = View.VISIBLE

                        Picasso.get().load(myMsgs[position].text)
                            .into(holder.mainImage)
                    }else{
                        holder.layoutFile?.visibility = View.VISIBLE
                        holder.layoutMessage?.visibility = View.GONE
                        holder.mainImage?.visibility = View.GONE
                        holder.nameFile?.text = myMsgs[position].fileNameFromURL
                        holder.sizeFile?.text = myMsgs[position].sizeFile.toString()
                    }

                } else {
                    holder.layoutMessage?.visibility = View.VISIBLE
                    holder.mainImage?.visibility = View.GONE
                    holder.bodyText?.text = myMsgs[position].text
                    holder.layoutFile?.visibility = View.GONE
                }
            }
            1 -> {
                (holder as ViewHolderDifferentMessage).mainImage?.setOnClickListener {
                    listener.clickPhoto(myMsgs[position].text)
                }

                holder.mainImage?.setShapeAppearanceModel(
                    holder.mainImage?.shapeAppearanceModel
                        ?.toBuilder()
                        ?.setTopRightCorner(CornerFamily.ROUNDED, (16 * Resources.getSystem().displayMetrics.density))
                        ?.setTopLeftCorner(CornerFamily.ROUNDED, (16 * Resources.getSystem().displayMetrics.density))
                        ?.setBottomRightCorner(CornerFamily.ROUNDED, (16 * Resources.getSystem().displayMetrics.density))
                        ?.setBottomLeftCorner(CornerFamily.ROUNDED, (2* Resources.getSystem().displayMetrics.density))
                    !!.build())


                if (myMsgs[position].isFile) {

                    if(myMsgs[position].isImage){
                        holder.layoutFile?.visibility = View.GONE
                        holder.bodyText?.visibility = View.GONE
                        holder.layoutBodyMessage?.visibility=View.VISIBLE
                        holder.mainImage?.visibility = View.VISIBLE

                        Picasso.get().load(myMsgs[position].text)
                            .into((holder.mainImage))
                    }else{
                        holder.layoutFile?.visibility = View.VISIBLE

                        holder.bodyText?.visibility = View.GONE
                        holder.layoutBodyMessage?.visibility=View.GONE
                        holder.mainImage?.visibility = View.GONE
                        holder.nameFile?.text = myMsgs[position].fileNameFromURL
                        holder.sizeFile?.text = myMsgs[position].sizeFile.toString()
                    }
                } else {
                    holder.bodyText?.text = myMsgs[position].text
                    holder.bodyText?.visibility = View.VISIBLE
                    holder.mainImage?.visibility = View.GONE
                }

                val nameInGroups = myMsgs[position].user.name.split("/")
                val name: String

                name = if (nameInGroups.size > 1) {
                    nameInGroups[1]
                } else {
                    myMsgs[position].user.name.split("@")[0]
                }

                holder.name?.text = name

                setAvatar(myMsgs[position].user.name + "@moonshard.tech", holder.avatar!!)
            }
            2 -> {
                val nameInGroups = myMsgs[position].user.name.split("/")
                var name = ""

                name = if (nameInGroups.size > 1) {
                    nameInGroups[1]
                } else {
                    myMsgs[position].user.name.split("@")[0]
                }

                (holder as ViewHolderDifferentMessage).bodyText?.text = myMsgs[position].text
                //holder.name?.text = name + "присоединился к чату"
                holder.name?.text = myMsgs[position].text


                setAvatar(myMsgs[position].user.name + "@moonshard.tech", holder.avatar!!)
            }
        }
    }

    @SuppressLint("CheckResult")
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

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun add(message: GenericMessage) {
        myMsgs.add(message)
        notifyDataSetChanged() // to render the list we need to notify
    }

    /**
     * Adds message to bottom of list and scroll if needed.
     *
     * @param message message to add.
     * @param scroll  `true` if need to scroll list to bottom when message added.
     */
    fun addToStart(message: GenericMessage, scroll: Boolean) {
        /*
        val isNewMessageToday = !isPreviousSameDate(0, message.createdAt)
        if (isNewMessageToday) {
            myMsgs.add(0, message)
        }
         */
        val element = message
        myMsgs.add(0, element)
        //notifyItemRangeInserted(0, if (isNewMessageToday) 2 else 1)
        notifyItemRangeInserted(0, 1)
        if (scroll) {
            layoutManager.scrollToPosition(0)
        }
    }

    fun setMessages(messages: List<GenericMessage>, reverse: Boolean) {
        myMsgs = messages.toMutableList()
        if (reverse) myMsgs.reverse()
        notifyDataSetChanged()
        layoutManager.scrollToPosition(0)
    }

    fun addToEnd(messages: List<GenericMessage>, reverse: Boolean) {
        if (messages.isEmpty()) return

        if (reverse) Collections.reverse(messages)

        if (myMsgs.isNotEmpty()) {
            val lastItemPosition = myMsgs.size - 1
            val lastItem = myMsgs[lastItemPosition].createdAt as Date
            if (DateFormatter.isSameDay(
                    messages[0].createdAt,
                    lastItem
                )
            ) {
                myMsgs.removeAt(lastItemPosition)
                notifyItemRemoved(lastItemPosition)
            }
        }

        val oldSize = myMsgs.size
        generateDateHeaders(messages)
        notifyItemRangeInserted(oldSize, myMsgs.size - oldSize)
    }


    private fun isPreviousSameDate(position: Int, dateToCompare: Date): Boolean {
        if (myMsgs.size <= position) return true
        val previousPositionDate = myMsgs[position].createdAt
        return DateFormatter.isSameDay(
            dateToCompare,
            previousPositionDate
        )
    }


    fun generateDateHeaders(messages: List<GenericMessage>) {
        for (i in messages.indices) {
            val message = messages[i]
            //this.myMsgs.add(message)
            if (messages.size > i + 1) {
                val nextMessage = messages[i + 1]
                if (!DateFormatter.isSameDay(
                        message.createdAt,
                        nextMessage.createdAt
                    )
                ) {
                    this.myMsgs.add((message))
                }
            } else {
                this.myMsgs.add(message)
            }
        }
    }

    /**
     * Interface definition for a callback to be invoked when next part of messages need to be loaded.
     */
    interface OnLoadMoreListener {

        /**
         * Fires when user scrolled to the end of list.
         *
         * @param page            next page to download.
         * @param totalItemsCount current items count.
         */
        fun onLoadMore(page: Int, totalItemsCount: Int)
    }

    override fun getItemCount(): Int = myMsgs.size

    inner class ViewHolderMyMessage(view: View) : RecyclerView.ViewHolder(view) {
        internal var bodyText: TextView? = view.findViewById(R.id.message_body)
        internal var mainImage: ShapeableImageView? = view.findViewById(R.id.mainImage)
        internal var layoutMessage: LinearLayout? = view.findViewById(R.id.layoutMessage)
        internal var layoutFile: RelativeLayout? = view.findViewById(R.id.layoutFile)
        internal var nameFile: TextView? = view.findViewById(R.id.nameFile)
        internal var sizeFile: TextView? = view.findViewById(R.id.sizeFile)

    }


    inner class ViewHolderDifferentMessage(view: View) : RecyclerView.ViewHolder(view) {
        internal var avatar: ImageView? = view.findViewById(R.id.avatar)
        internal var name: TextView? = view.findViewById(R.id.name)
        internal var bodyText: TextView? = view.findViewById(R.id.message_body)
        internal var mainImage: ShapeableImageView? = view.findViewById(R.id.mainImage)

        internal var layoutFile: RelativeLayout? = view.findViewById(R.id.layoutFile)
        internal var layoutBodyMessage: RelativeLayout? = view.findViewById(R.id.layoutBodyMessage)
        internal var nameFile: TextView? = view.findViewById(R.id.nameFile)
        internal var sizeFile: TextView? = view.findViewById(R.id.sizeFile)

    }
}