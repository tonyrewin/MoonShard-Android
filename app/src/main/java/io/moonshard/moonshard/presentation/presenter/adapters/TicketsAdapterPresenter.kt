package io.moonshard.moonshard.presentation.presenter.adapters

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.moonshardwallet.models.Ticket
import com.orhanobut.logger.Logger
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.common.utils.Utils
import io.moonshard.moonshard.presentation.view.adapters.TicketsAdapterView
import io.moonshard.moonshard.ui.adapters.profile.present.TicketPresentAdapter
import io.moonshard.moonshard.ui.adapters.profile.present.TicketPresentListener
import io.moonshard.moonshard.usecase.EventsUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import org.jxmpp.jid.impl.JidCreate

@InjectViewState
class TicketsAdapterPresenter : MvpPresenter<TicketsAdapterView>() {

    private var tickets: ArrayList<Ticket> = arrayListOf()

    private var eventsUseCase: EventsUseCase? = null

    init {
        eventsUseCase = EventsUseCase()
    }

    fun onBindViewHolder(
        holder: TicketPresentAdapter.ViewHolder,
        position: Int,
        listener: TicketPresentListener
    ) {

        try {
            val ticket = tickets[position]
            holder.titleTypeTv?.text = getNameEvent(ticket.jidEvent)

            getTicketTypeName(ticket.jidEvent, ticket.ticketType.toInt(), holder.typeTicket!!)
            holder.numberTicketTv?.text = ticket.ticketId.toString()
            setAvatar(ticket.jidEvent, getNameEvent(ticket.jidEvent), holder.avatarTicket!!)

            if (ticket.payState.toInt() == 2) {
                holder.scannedIv?.visibility = View.VISIBLE
            } else {
                holder.scannedIv?.visibility = View.GONE
            }

            if (position == tickets.size - 1) {
                val params = holder.mainLayout?.layoutParams as? ViewGroup.MarginLayoutParams
                params?.marginStart = Utils.convertDpToPixel(8F, holder.mainLayout?.context)
                params?.marginEnd = Utils.convertDpToPixel(8F, holder.mainLayout?.context)
                params?.bottomMargin = Utils.convertDpToPixel(8F, holder.mainLayout?.context)
                params?.topMargin = Utils.convertDpToPixel(8F, holder.mainLayout?.context)
            }

            holder.mainLayout?.setOnClickListener {
                listener.click(tickets[position], holder.typeTicket!!.text.toString())
            }
        } catch (e: Exception) {
            Logger.d(e)
        }
    }

    private fun getTicketTypeName(eventID: String, typeID: Int, ticketTypeName: TextView) {
        Log.d("getTicketTypeName", "$eventID, $typeID")
        eventsUseCase!!.getTicketTypeName(eventID, typeID)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { ticketType, throwable ->
                if (throwable == null) {
                    ticketTypeName.text = ticketType.typeName
                    Logger.d(ticketType)
                } else {
                    ticketTypeName.text = "Билет"
                    throwable.message?.let { Logger.e(throwable.message!!) }
                }
            }
    }

    fun getNameEvent(jid: String): String {
        val jidEvent = JidCreate.entityBareFrom(jid)

        val muc = MainApplication.getXmppConnection().multiUserChatManager
            .getMultiUserChat(jidEvent)

        val info = MainApplication.getXmppConnection().multiUserChatManager
            .getRoomInfo(muc.room)

        return info.name
    }

    fun setData(tickets: List<Ticket>) {
        this.tickets.clear()
        this.tickets.addAll(tickets)
        viewState.onDataChange()
    }

    fun getChatListSize(): Int {
        return tickets.size
    }

    @SuppressLint("CheckResult")
    private fun setAvatar(jid: String, nameChat: String, imageView: ImageView) {
        if (MainApplication.getCurrentChatActivity() != jid) {
            MainApplication.getXmppConnection().loadAvatarForTicket(jid, nameChat)
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
                }, { throwable ->
                    throwable.message?.let { Logger.e(it) }
                })
        }
    }
}