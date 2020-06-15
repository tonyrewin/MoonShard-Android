package io.moonshard.moonshard.presentation.presenter.adapters

import android.util.Log
import android.view.View
import android.widget.TextView
import com.example.moonshardwallet.models.MyTicketSale
import com.orhanobut.logger.Logger
import io.moonshard.moonshard.presentation.view.adapters.ConfirmTicketsBuyAdapterView
import io.moonshard.moonshard.ui.adapters.tickets.ConfirmTicketsBuyAdapter
import io.moonshard.moonshard.ui.adapters.tickets.ConfirmTicketsBuyListener
import io.moonshard.moonshard.usecase.EventsUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter

@InjectViewState
class ConfirmTicketsBuyAdapterPresenter : MvpPresenter<ConfirmTicketsBuyAdapterView>() {
    private var ticketSales: ArrayList<MyTicketSale> = arrayListOf()

    private var eventsUseCase: EventsUseCase? = null

    init {
        eventsUseCase = EventsUseCase()
    }

    fun onBindViewHolder(
        holder: ConfirmTicketsBuyAdapter.ViewHolder,
        position: Int,
        listener: ConfirmTicketsBuyListener, idChat: String
    ) {
        try {
            holder.typeTicketTv?.text = ticketSales[holder.adapterPosition].typeTicket.toString()
            holder.costTicketTv?.text = ticketSales[holder.adapterPosition].priceTicket + " ₽"
            getTicketTypeName(idChat,ticketSales[position].typeTicket.toInt(),holder.typeTicketTv!!)
            if (position == ticketSales.size - 1) {
                holder.viewLine?.visibility = View.GONE
            }
        } catch (e: Exception) {
            Logger.d(e)
        }
    }

    fun setData(ticketSales: List<MyTicketSale>) {
        this.ticketSales.clear()
        this.ticketSales.addAll(ticketSales)
        viewState.onDataChange()
    }

    fun getChatListSize(): Int {
        return ticketSales.size
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
                    throwable.message?.let { Logger.e(throwable.message!!) }
                }
            }
    }
}

