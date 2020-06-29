package io.moonshard.moonshard.presentation.presenter.chat.info.tickets

import android.util.Log
import com.example.moonshardwallet.MainService
import com.example.moonshardwallet.models.MyTicketSale
import com.google.gson.Gson
import com.orhanobut.logger.Logger
import io.moonshard.moonshard.common.utils.DateHolder
import io.moonshard.moonshard.models.api.RoomPin
import io.moonshard.moonshard.models.dbEntities.ChatEntity
import io.moonshard.moonshard.presentation.view.chat.info.tickets.ConfirmBuyTicketsView
import io.moonshard.moonshard.repository.ChatListRepository
import io.moonshard.moonshard.ui.fragments.map.RoomsMap
import io.moonshard.moonshard.ui.fragments.mychats.chat.info.tickets.buyticket.BuyTicketObject
import io.moonshard.moonshard.ui.fragments.mychats.chat.info.tickets.buyticket.MyValue
import io.moonshard.moonshard.usecase.EventsUseCase
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import org.jxmpp.jid.impl.JidCreate
import java.util.concurrent.TimeUnit

@InjectViewState
class ConfirmBuyTicketsPresenter : MvpPresenter<ConfirmBuyTicketsView>() {

    private var useCase: EventsUseCase? = null

    private val compositeDisposable = CompositeDisposable()

    init {
        useCase = EventsUseCase()
    }


    private var costAll: String? = null


    fun getConfirmTickets() {
        val ticketSales = BuyTicketObject.ticketSales


        val ticketsConfirm = arrayListOf<MyTicketSale>()
        for ((key, value) in ticketSales) {
            for (i in 0 until value) {
                ticketsConfirm.add(key)
            }
        }
        getCostAllTicket()
        getAmountAllTicket()

        viewState?.setTickets(ticketsConfirm)
    }

    private fun getCostAllTicket() {
        val costAll = getCostAll()
        viewState?.showCost(costAll)
    }

    private fun getCostAll(): String {
        var costAll: Double = 0.0
        for (key in BuyTicketObject.ticketSales) {
            val ticketSaleValue = key.value
            var cost = ticketSaleValue * key.key.priceTicket.toDouble()
            costAll = costAll + cost
        }
        this.costAll = costAll.toInt().toString()
        return costAll.toInt().toString()
    }

    private fun getAmountAllTicket() {
        var amountAll: Int = 0
        for (key in BuyTicketObject.ticketSales) {
            val ticketSaleValue = key.value
            amountAll = amountAll + ticketSaleValue
        }
        viewState?.showAmount(amountAll.toString())
    }

    fun buyTicketsRxMain() {
        viewState?.showProgressBar()

        val values = arrayListOf<MyValue>()
        BuyTicketObject.ticketSales.forEach { (key, value) ->
            if(value!=0) values.add(MyValue(key, value))
        }

        for(i in values.indices){
            Log.d("testKek",values[i].toString() + values[i].myTicketSale.originSaleAddress + " , " + values[i].value)
        }

        Observable.fromIterable(values).concatMapSingle {
            (MainService.getBuyTicketService()
                .buyRx(it.myTicketSale.originSaleAddress, it.value)).delay(1, TimeUnit.SECONDS)
            }
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.doOnComplete {
                Log.d("eventTxTicket transaction ", "test")
                viewState?.showSuccessScreen(costAll)
            }
            ?.subscribe( {
                Log.d("eventTxTicket hash: ", it.blockHash)
            },{
                Log.d("eventTxTicket error: ", it.message)
                viewState?.hideProgressBar()
                viewState?.showToast("Произошла ошибка")
            })
    }

    fun getEventInfo(jid: String) {
        viewState?.showProgressBar()
        ChatListRepository.getChatByJidSingle(JidCreate.from(jid))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ chatEntity ->
                var eventId: String? = getEventId(jid)

                if (eventId == null) {
                    if (chatEntity.event != null) {

                        val event = Gson().fromJson(chatEntity.event, RoomPin::class.java)
                        val date = DateHolder(event?.eventStartDate!!)
                        val startDateEvent =
                            "${date.dayOfMonth} ${date.getMonthString(date.month)} ${date.year} г. в ${date.hour}:${date.minute}"
                        viewState?.showEventInfo(event.name!!, startDateEvent, event.address!!)
                        viewState?.hideProgressBar()
                    }
                } else {
                    getEvent(eventId, chatEntity)
                }
            }, {
                viewState?.hideProgressBar()
                viewState?.showToast("Произошла ошибка")
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
                    //work with event
                    val date = DateHolder(event?.eventStartDate!!)
                    val startDateEvent =
                        "${date.dayOfMonth} ${date.getMonthString(date.month)} ${date.year} г. в ${date.hour}:${date.minute}"
                    viewState?.showEventInfo(event.name!!, startDateEvent, event.address!!)
                } else {
                    val eventFromBd = Gson().fromJson(chatEntity.event, RoomPin::class.java)
                    val date = DateHolder(eventFromBd?.eventStartDate!!)
                    val startDateEvent =
                        "${date.dayOfMonth} ${date.getMonthString(date.month)} ${date.year} г. в ${date.hour}:${date.minute}"
                    viewState?.showEventInfo(
                        eventFromBd.name!!,
                        startDateEvent,
                        eventFromBd.address!!
                    )
                }
                viewState?.hideProgressBar()
            })
    }
}