package io.moonshard.moonshard.presentation.presenter.adapters

import android.util.Log
import android.view.ViewGroup
import android.widget.TextView
import com.example.moonshardwallet.models.MyTicketSale
import com.orhanobut.logger.Logger
import io.moonshard.moonshard.common.utils.Utils
import io.moonshard.moonshard.presentation.view.adapters.TypesTicketAdapterView
import io.moonshard.moonshard.ui.adapters.tickets.TypesTicketAdapter
import io.moonshard.moonshard.ui.adapters.tickets.TypesTicketListener
import io.moonshard.moonshard.usecase.EventsUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter

@InjectViewState
class TypesTicketAdapterPresenter : MvpPresenter<TypesTicketAdapterView>() {

    private var ticketSales: ArrayList<MyTicketSale> = arrayListOf()

    private var eventsUseCase: EventsUseCase? = null

    init {
        eventsUseCase = EventsUseCase()
    }

    fun onBindViewHolder(
        holder: TypesTicketAdapter.ViewHolder,
        position: Int,
        listener: TypesTicketListener,
        idChat: String
    ) {
        try {
            holder.typeTicketTv?.text =
                "Название билета: " + ticketSales[position].typeTicket.toString()
            holder.priceTicketTv?.text = ticketSales[position].priceTicket.toString() + " ₽"
            holder.limitTv?.text = ticketSales[position].saleLimit.toString() + " билетов"
            getTicketTypeName(idChat,ticketSales[position].typeTicket.toInt(),holder.typeTicketTv!!)

            if (position == ticketSales.size - 1) {
                val params = holder.mainLayout?.layoutParams as ViewGroup.MarginLayoutParams
                params.marginStart = Utils.convertDpToPixel(8F, holder.mainLayout?.context)
                params.marginEnd = Utils.convertDpToPixel(8F, holder.mainLayout?.context)
                params.bottomMargin = Utils.convertDpToPixel(8F, holder.mainLayout?.context)
                params.topMargin = Utils.convertDpToPixel(8F, holder.mainLayout?.context)
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
                    throwable.message?.let { Logger.e(throwable.message!!) }
                }
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
}