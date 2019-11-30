package io.moonshard.moonshard.ui.adapters.chat

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.moonshard.moonshard.R
import io.moonshard.moonshard.models.GenericMessage
import io.moonshard.moonshard.ui.activities.RecyclerScrollMoreListener
import io.moonshard.moonshard.ui.adapters.DateFormatter
import java.util.*


open class MessagesAdapter(
    private var myMsgs: ArrayList<GenericMessage>, val layoutManager: LinearLayoutManager
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), RecyclerScrollMoreListener.OnLoadMoreListener {

    private var loadMoreListener: OnLoadMoreListener? = null

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
        if (myMsgs[position].isBelongsToCurrentUser) {
            return 0
        } else {
            return 1
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            0 -> {
                (holder as ViewHolderMyMessage).bodyText?.text = myMsgs[position].text
            }
            1 -> {
                val nameInGroups = myMsgs[position].user.name.split("/")
                var name = ""

                name = if (nameInGroups.size > 1) {
                    nameInGroups[1]
                } else {
                    myMsgs[position].user.name.split("@")[0]
                }

                (holder as ViewHolderDifferentMessage).bodyText?.text = myMsgs[position].text
                holder.name?.text = name
                holder.avatar?.setBackgroundColor(Color.parseColor("#7CFC00"))
            }
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
        val isNewMessageToday = !isPreviousSameDate(0, message.createdAt)
        if (isNewMessageToday) {
            myMsgs.add(0, message)
        }
        val element = message
        myMsgs.add(0, element)
        notifyItemRangeInserted(0, if (isNewMessageToday) 2 else 1)
        if (layoutManager != null && scroll) {
            layoutManager.scrollToPosition(0)
        }
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
        if (myMsgs.size <= position) return false
        val previousPositionDate = myMsgs[position].createdAt
        return DateFormatter.isSameDay(
            dateToCompare,
            previousPositionDate
        )
    }


    fun generateDateHeaders(messages: List<GenericMessage>) {
        for (i in messages.indices) {
            val message = messages[i]
            this.myMsgs.add(message)
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
    }

    inner class ViewHolderDifferentMessage(view: View) : RecyclerView.ViewHolder(view) {
        internal var avatar: ImageView? = view.findViewById(R.id.avatar)
        internal var name: TextView? = view.findViewById(R.id.name)
        internal var bodyText: TextView? = view.findViewById(R.id.message_body)
    }
}