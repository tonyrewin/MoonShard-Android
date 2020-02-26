package io.moonshard.moonshard.ui.adapters.chat

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.CornerFamily
import com.orhanobut.logger.Logger
import com.squareup.picasso.Picasso
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.R
import io.moonshard.moonshard.StreamUtil
import io.moonshard.moonshard.models.GenericMessage
import io.moonshard.moonshard.ui.activities.RecyclerScrollMoreListener
import io.moonshard.moonshard.ui.adapters.DateFormatter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import trikita.log.Log
import zlc.season.rxdownload4.download
import zlc.season.rxdownload4.file
import java.io.File
import java.util.*

interface PhotoListener {
    fun clickPhoto(url: String)
    fun clickUserAvater(jid:String)
}

open class MessagesAdapter(
    private var myMsgs: MutableList<GenericMessage>,
    val layoutManager: LinearLayoutManager,
    val listener: PhotoListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), RecyclerScrollMoreListener.OnLoadMoreListener {

    private var loadMoreListener: OnLoadMoreListener? = null

    var isImageFitToScreen: Boolean = false

    var disposable: Disposable? = null

    val fileStatus: String = "not_downloading"

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
                        ?.setTopRightCorner(
                            CornerFamily.ROUNDED,
                            (16 * Resources.getSystem().displayMetrics.density)
                        )
                        ?.setTopLeftCorner(
                            CornerFamily.ROUNDED,
                            (16 * Resources.getSystem().displayMetrics.density)
                        )
                        ?.setBottomRightCorner(
                            CornerFamily.ROUNDED,
                            (2 * Resources.getSystem().displayMetrics.density)
                        )
                        ?.setBottomLeftCorner(
                            CornerFamily.ROUNDED,
                            (16 * Resources.getSystem().displayMetrics.density)
                        )
                    !!.build()
                )

                if (myMsgs[position].isFile) {
                    if (myMsgs[position].isImage) {
                        holder.layoutFile?.visibility = View.GONE
                        holder.layoutMessage?.visibility = View.GONE
                        holder.mainImage?.visibility = View.VISIBLE

                        Glide.with(holder.itemView.context).load(myMsgs[position].text)
                            .into(holder.mainImage!!)
                    } else {
                        holder.layoutFile?.visibility = View.VISIBLE
                        holder.layoutMessage?.visibility = View.GONE
                        holder.mainImage?.visibility = View.GONE
                        holder.nameFile?.text = myMsgs[position].fileNameFromURL
                        getSizeFile(myMsgs[position].text,holder.sizeFile)

                        val fileInStorage = myMsgs[position].text.file()

                        if (fileInStorage.isFile) {
                            holder.statusFileIv?.setImageResource(R.drawable.ic_file)

                            holder.layoutFile?.setOnClickListener {
                                openFile(fileInStorage, it.context)
                            }
                        } else {
                            holder.statusFileIv?.setImageResource(R.drawable.ic_download_file)

                            holder.layoutFile?.setOnClickListener {
                                downloadFile(
                                    myMsgs[position].text,
                                    holder.sizeFile!!,
                                    holder.statusFileIv,
                                    holder.progressBarFile,
                                    holder.layoutFile
                                )
                            }
                        }
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
                        ?.setTopRightCorner(
                            CornerFamily.ROUNDED,
                            (16 * Resources.getSystem().displayMetrics.density)
                        )
                        ?.setTopLeftCorner(
                            CornerFamily.ROUNDED,
                            (16 * Resources.getSystem().displayMetrics.density)
                        )
                        ?.setBottomRightCorner(
                            CornerFamily.ROUNDED,
                            (16 * Resources.getSystem().displayMetrics.density)
                        )
                        ?.setBottomLeftCorner(
                            CornerFamily.ROUNDED,
                            (2 * Resources.getSystem().displayMetrics.density)
                        )
                    !!.build()
                )

                if (myMsgs[position].isFile) {

                    if (myMsgs[position].isImage) {
                        holder.layoutFile?.visibility = View.GONE
                        holder.bodyText?.visibility = View.GONE
                        holder.layoutBodyMessage?.visibility = View.VISIBLE
                        holder.mainImage?.visibility = View.VISIBLE

                        Glide.with(holder.itemView.context).load(myMsgs[position].text)
                            .into(holder.mainImage!!)
                    } else {
                        holder.layoutFile?.visibility = View.VISIBLE

                        holder.bodyText?.visibility = View.GONE
                        holder.layoutBodyMessage?.visibility = View.GONE
                        holder.mainImage?.visibility = View.GONE
                        holder.nameFile?.text = myMsgs[position].fileNameFromURL
                        getSizeFile(myMsgs[position].text,holder.sizeFile)


                        val fileInStorage = myMsgs[position].text.file()

                        if (fileInStorage.isFile) {
                            holder.statusFileIv?.setImageResource(R.drawable.ic_file)

                            holder.layoutFile?.setOnClickListener {
                                openFile(fileInStorage, it.context)
                            }
                        } else {
                            holder.statusFileIv?.setImageResource(R.drawable.ic_download_file)

                            holder.layoutFile?.setOnClickListener {
                                downloadFile(
                                    myMsgs[position].text,
                                    holder.sizeFile!!,
                                    holder.statusFileIv,
                                    holder.progressBarFile,
                                    holder.layoutFile
                                )
                            }
                        }

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

                holder.avatar?.setOnClickListener {
                    listener.clickUserAvater(myMsgs[position].user.jid)
                }
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

                holder.avatar?.setOnClickListener {
                    listener.clickUserAvater(myMsgs[position].user.jid)
                }
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
        val element = message
        myMsgs.add(0, element)
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

    fun openFile(file: File, context: Context) {
        try {
            val uri = FileProvider.getUriForFile(
                context,
                context.applicationContext.packageName + ".provider",
                file
            )
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            context.startActivity(intent)
        } catch (e: Exception) {
            Logger.d(e)
        }
    }

    fun getSizeFile(url: String,textView: TextView?) {
        StreamUtil.getSizeFile(url).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe ({
                textView?.text = it
            },{
            })
    }

    fun downloadFile(
        url: String,
        textSize: TextView,
        statusFileIv: ImageView?,
        progressBarFile: ProgressBar?,
        layoutFile: RelativeLayout?
    ) {
        statusFileIv?.setImageResource(R.drawable.ic_close_file)
        progressBarFile?.visibility = View.VISIBLE

        layoutFile?.setOnClickListener {
            stopDownloadingFile()
            statusFileIv?.setImageResource(R.drawable.ic_download_file)
        }

        disposable = url.download()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = { progress ->
                    textSize.text = "${progress.downloadSizeStr()} MB }"
                },
                onComplete = {
                    progressBarFile?.visibility = View.GONE
                    statusFileIv?.setImageResource(R.drawable.ic_file)
                    textSize.text = "SUCCESS"

                    layoutFile?.setOnClickListener {
                        openFile(url.file(), it.context)
                    }
                },
                onError = {
                    statusFileIv?.setImageResource(R.drawable.ic_download_file)
                    progressBarFile?.visibility = View.GONE
                    textSize.text = "ERROR"
                }
            )
    }

    fun stopDownloadingFile() {
        disposable?.dispose()
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
        internal var statusFileIv: ImageView? = view.findViewById(R.id.statusFileIv)
        internal var progressBarFile: ProgressBar? = view.findViewById(R.id.progressBarFile)
    }

    inner class ViewHolderDifferentMessage(view: View) : RecyclerView.ViewHolder(view) {
        internal var avatar: ImageView? = view.findViewById(R.id.avatar)
        internal var name: TextView? = view.findViewById(R.id.name)
        internal var bodyText: TextView? = view.findViewById(R.id.message_body)
        internal var mainImage: ShapeableImageView? = view.findViewById(R.id.mainImage)
        internal var layoutFile: RelativeLayout? = view.findViewById(R.id.layoutFile)
        internal var layoutBodyMessage: LinearLayout? = view.findViewById(R.id.layoutBodyMessage)
        internal var nameFile: TextView? = view.findViewById(R.id.nameFile)
        internal var sizeFile: TextView? = view.findViewById(R.id.sizeFile)
        internal var statusFileIv: ImageView? = view.findViewById(R.id.statusFileIv)
        internal var progressBarFile: ProgressBar? = view.findViewById(R.id.progressBarFile)
    }
}