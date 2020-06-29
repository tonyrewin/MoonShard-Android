package io.moonshard.moonshard.presentation.presenter.chat.info.tickets

import android.util.Log
import com.example.moonshardwallet.MainService
import com.example.moonshardwallet.contracts.TicketFactory721
import com.orhanobut.logger.Logger
import io.moonshard.moonshard.common.NotFoundException
import io.moonshard.moonshard.presentation.view.chat.info.tickets.AddNewTypeTicketView
import io.moonshard.moonshard.repository.ChatListRepository
import io.moonshard.moonshard.usecase.EventsUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import java.math.BigInteger

@InjectViewState
class AddNewTypeTicketPresenter : MvpPresenter<AddNewTypeTicketView>() {

    private var eventsUseCase: EventsUseCase? = null
    private val compositeDisposable = CompositeDisposable()

    init {
        eventsUseCase = EventsUseCase()
    }

    fun addNewEvent(
        typeString: String,
        price: String,
        limit: String,
        jidEvent: String
    ) {
        viewState?.showProgressBar()
        Log.d("addNewEvent", jidEvent)
        val sizeTicketSales = MainService.getBuyTicketService().sizeTicketSales(jidEvent)
        Log.d("addNewEvent", sizeTicketSales.toString())

        if (sizeTicketSales == null || sizeTicketSales == 0) {
            MainService.getBuyTicketService()
                .createTicketSale(BigInteger(price), jidEvent, BigInteger(limit))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    //всегда  размер массива 1
                    val eventTx: TicketFactory721.SaleCreatedHumanEventResponse? =
                        MainService.getWalletService().ticketfactory.getSaleCreatedHumanEvents(it)[0]
                    val typeInt = eventTx?.ticket_type

                    if (typeInt != null) {
                        createTicketTypeName(jidEvent, typeString, typeInt.toInt())
                    }
                    Log.d("createTicketSale tx: ", it.blockHash)
                }, { throwable ->
                    throwable.message?.let { Logger.e(it) }
                    viewState?.showToast("Произошла ошибка")
                })
        } else {
            val originSale = MainService.getBuyTicketService().getEventIdFromFirstSale(jidEvent)
            Log.d("originSale", originSale)

            MainService.getBuyTicketService()
                .createNewType(originSale, BigInteger(price), BigInteger(limit))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                     val eventTx: TicketFactory721.PluggedSaleHumanEventResponse? = MainService.getWalletService().ticketfactory.getPluggedSaleHumanEvents(it)[0]

                     val typeInt = eventTx?.ticket_type

                     if (typeInt != null) {
                         createTicketTypeName(jidEvent, typeString, typeInt.toInt())
                     }
                     Log.d("plugIn tx: ", eventTx?.ticket_type.toString())
                }, { throwable ->
                    throwable.message?.let { Logger.e(it) }
                    viewState?.showToast("Произошла ошибка")
                })
        }
    }

    private fun createTicketTypeName(eventID: String, typeName: String, typeID: Int) {
        Log.d("trash_trans: ", eventID)
        Log.d("trash_trans tx: ", typeName)
        Log.d("trash_trans tx: ", typeID.toString())

        compositeDisposable.add(eventsUseCase!!.createTicketTypeName(eventID, typeName, typeID)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { event, throwable ->
                if (throwable == null) {
                    Logger.d(event)
                    viewState?.back()
                } else {
                    throwable.message?.let { Logger.e(it) }
                    viewState?.showToast("Произошла ошибка")
                }
            })
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}