package io.moonshard.moonshard.presentation.presenter.adapters

import android.util.Log
import android.view.View
import android.widget.TextView
import com.example.moonshardwallet.models.MyTicketSale
import com.orhanobut.logger.Logger
import io.moonshard.moonshard.presentation.view.adapters.BuyTicketsAdapterView
import io.moonshard.moonshard.presentation.view.adapters.ConfirmTicketsBuyAdapterView
import io.moonshard.moonshard.ui.adapters.tickets.BuyTicketsAdapter
import io.moonshard.moonshard.ui.adapters.tickets.ConfirmTicketsBuyAdapter
import io.moonshard.moonshard.ui.adapters.tickets.ConfirmTicketsBuyListener
import io.moonshard.moonshard.ui.adapters.tickets.TicketListener
import io.moonshard.moonshard.usecase.EventsUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter

@InjectViewState
class BuyTicketsAdapterPresenter : MvpPresenter<BuyTicketsAdapterView>() {
    private var ticketSales: ArrayList<MyTicketSale> = arrayListOf()

    private var eventsUseCase: EventsUseCase? = null

    init {
        eventsUseCase = EventsUseCase()
    }

    fun onBindViewHolder(
        holder: BuyTicketsAdapter.ViewHolder,
        position: Int,
        listener: TicketListener, idChat: String
    ) {
        Log.d("BuyTicketsFragment3",idChat)

        try {
            getTicketTypeName(idChat,ticketSales[position].typeTicket.toInt(),holder.typeTicketTv!!)

            holder.costTicketTv?.text = ticketSales[position].priceTicket + " ₽"

            holder.plusBtn?.setOnClickListener {
                holder.counterTv?.text =  (holder.counterTv?.text.toString().toInt() + 1).toString()
                listener.clickPlus(ticketSales[holder.adapterPosition])
            }

            holder.minusBtn?.setOnClickListener {
                if(holder.counterTv?.text!="0"){
                    holder.counterTv?.text =  (holder.counterTv?.text.toString().toInt() - 1).toString()
                    listener.clickMinus(ticketSales[holder.adapterPosition])
                }
            }

            holder.itemView.setOnClickListener {
                listener.click(ticketSales[position].originSaleAddress)
            }

            if(position == ticketSales.size-1){
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