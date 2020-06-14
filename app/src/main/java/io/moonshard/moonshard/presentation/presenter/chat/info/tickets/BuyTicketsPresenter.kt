package io.moonshard.moonshard.presentation.presenter.chat.info.tickets

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.moonshardwallet.MainService
import com.example.moonshardwallet.models.MyTicketSale
import com.google.gson.Gson
import com.orhanobut.logger.Logger
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.common.utils.DateHolder
import io.moonshard.moonshard.models.api.RoomPin
import io.moonshard.moonshard.models.dbEntities.ChatEntity
import io.moonshard.moonshard.presentation.view.chat.info.tickets.BuyTicketsView
import io.moonshard.moonshard.repository.ChatListRepository
import io.moonshard.moonshard.ui.fragments.map.RoomsMap
import io.moonshard.moonshard.ui.fragments.mychats.chat.info.tickets.buyticket.BuyTicketObject
import io.moonshard.moonshard.usecase.EventsUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import org.jxmpp.jid.impl.JidCreate

@InjectViewState
class BuyTicketsPresenter : MvpPresenter<BuyTicketsView>() {
    private var useCase: EventsUseCase? = null

    private val compositeDisposable = CompositeDisposable()

    init {
        useCase = EventsUseCase()
    }

    fun getTypesTicket(eventJid: String) {
        MainService.getBuyTicketService().getTicketsTypes(eventJid)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { typesTicket, throwable ->
                if (throwable == null) {
                    for (i in typesTicket.indices) {
                        BuyTicketObject.ticketSales.put(typesTicket[i], 0)
                    }
                    viewState.setTickets(typesTicket)
                } else {
                    throwable.message?.let { viewState?.showToast(it) }
                }
            }


    }

    fun buyTicket(saleAddress: String, amount: Int) {
        MainService.getBuyTicketService().buy(saleAddress, amount).thenAccept {
            // var events_tx = sale_instance.getTokensPurchasedEvents(transactionReceipt);
            // do somthing with event response
            var event_tx_ticket =
                MainService.getBuyTicketService().ticket.getTicketBoughtHumanEvents(it)
            var test = ""

            var tickets = MainService.getBuyTicketService().myTicketsByOwner
        }.exceptionally { e ->
            null
        }
    }

    fun plusTicketSale(ticketSale: MyTicketSale) {
        val value = BuyTicketObject.ticketSales[ticketSale]
        val amount = value!! + 1
        BuyTicketObject.ticketSales.put(ticketSale, amount)
        viewState?.showCost(getCostAllTicket())
        viewState?.showAmount(getAmountAllTicket())
    }

    fun minusTicketSale(ticketSale: MyTicketSale) {
        val value = BuyTicketObject.ticketSales[ticketSale]
        if (value!! > 0) {
            val amount = value - 1
            BuyTicketObject.ticketSales.put(ticketSale, amount)
            viewState?.showCost(getCostAllTicket())
            viewState?.showAmount(getAmountAllTicket())
        }
    }

    private fun  getCostAllTicket():String {
        var amountAll: Double = 0.0
        for (key in BuyTicketObject.ticketSales) {
            val ticketSaleValue = key.value
            var amount = ticketSaleValue * key.key.priceTicket.toDouble()
            amountAll = amountAll+amount
        }
        return amountAll.toInt().toString()
    }

    fun  getAmountAllTicket():String {
        var amountAll: Int = 0
        for (key in BuyTicketObject.ticketSales) {
            val ticketSaleValue = key.value
            amountAll = amountAll + ticketSaleValue
        }
        return amountAll.toString()
    }

    fun getRoomInfo(jid: String) {
        viewState?.showProgressBar()

        ChatListRepository.getChatByJidSingle(JidCreate.from(jid))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ chatEntity ->
                var eventId: String? = getEventId(jid)

                if (eventId == null) {
                    if(chatEntity.event!=null){
                        val event = Gson().fromJson(chatEntity.event,RoomPin::class.java)
                        viewState?.hideProgressBar()
                        viewState?.showNameEvent(event?.name)
                        val date = DateHolder(event!!.eventStartDate!!)
                        viewState.showStartDateEvent("${date.dayOfMonth} ${date.getMonthString(date.month)} ${date.year} г. в ${date.hour}:${date.minute}")
                        setAvatar(jid, event!!.name!!)
                    }
                } else {
                    getEvent(jid, eventId,chatEntity)
                }
            }, {
                viewState?.hideProgressBar()
                Logger.d(it)
            })
    }

    private fun getEventId(jid: String): String? {
        for (i in RoomsMap.rooms.indices) {
            if (jid == RoomsMap.rooms[i].roomID) {
                return RoomsMap.rooms[i].id
            }
        }
        return null
    }

    private fun getEvent(
        jid: String,
        eventId: String,
        chatEntity: ChatEntity
    ) {
        compositeDisposable.add(useCase!!.getRoom(
            eventId
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { event, throwable ->
                if (throwable == null) {
                    viewState?.hideProgressBar()
                    viewState?.showNameEvent(event.name)
                    val date = DateHolder(event?.eventStartDate!!)
                    viewState.showStartDateEvent("${date.dayOfMonth} ${date.getMonthString(date.month)} ${date.year} г. в ${date.hour}:${date.minute}")
                    setAvatar(jid,event.name!!)
                } else {
                    val eventFromBd = Gson().fromJson(chatEntity.event,RoomPin::class.java)
                    viewState?.hideProgressBar()
                    viewState?.showNameEvent(eventFromBd.name)
                    val date = DateHolder(eventFromBd?.eventStartDate!!)
                    viewState.showStartDateEvent("${date.dayOfMonth} ${date.getMonthString(date.month)} ${date.year} г. в ${date.hour}:${date.minute}")
                    setAvatar(jid,eventFromBd.name!!)
                    Logger.d(throwable)
                }
            })
    }

    private fun setAvatar(jid: String, nameChat: String) {
        if (MainApplication.getCurrentChatActivity() != jid) {
            MainApplication.getXmppConnection().loadAvatarForTicket(jid, nameChat)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ bytes ->
                    val avatar: Bitmap?
                    if (bytes != null) {
                        avatar = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                        viewState?.showAvatarEvent(avatar)
                    }
                }, { throwable ->
                    throwable.message?.let { Logger.e(it) }
                })
        }
    }
}