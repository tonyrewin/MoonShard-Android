package io.moonshard.moonshard.presentation.presenter

import android.util.Log
import io.moonshard.moonshard.models.api.Category
import io.moonshard.moonshard.models.api.RoomPin
import io.moonshard.moonshard.presentation.view.ListChatMapView
import io.moonshard.moonshard.ui.fragments.map.RoomsMap
import io.moonshard.moonshard.usecase.EventsUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import java.util.*
import kotlin.collections.ArrayList

@InjectViewState
class ListChatMapPresenter : MvpPresenter<ListChatMapView>() {

    private var useCase: EventsUseCase? = null
    private val compositeDisposable = CompositeDisposable()

    private var events = ArrayList<RoomPin>()
    private var fullEvents = ArrayList<RoomPin>()

    init {
        useCase = EventsUseCase()
    }

    fun getChats() {
        if(RoomsMap.isFilter){
            if(RoomsMap.isFilterDate){

            }else{
                getRoomsByCategory("","","",RoomsMap.category!!)
            }
        }else{
            //this hard data - center Moscow
            compositeDisposable.add(useCase!!.getRooms("55.751244", "37.618423", 10000.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { rooms, throwable ->
                    if (throwable == null) {
                        RoomsMap.clean()
                        RoomsMap.rooms = rooms
                        events.clear()
                        fullEvents.clear()
                        events.addAll(rooms)
                        fullEvents.addAll(rooms)
                        Log.d("rooms", rooms.size.toString())
                        viewState?.setChats(events)
                    } else {
                        val error = ""
                    }
                })
        }
    }

    private fun getRoomsByCategory(lat: String, lng: String, radius: String, category: Category){
        compositeDisposable.add(useCase!!.getRoomsByCategory(category.id,"55.751244", "37.618423", 10000.toString())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { rooms, throwable ->
                if (throwable == null) {
                    RoomsMap.clean()
                    RoomsMap.rooms = rooms
                    events.clear()
                    fullEvents.clear()
                    events.addAll(rooms)
                    fullEvents.addAll(rooms)
                    Log.d("rooms", rooms.size.toString())
                    viewState?.setChats(events)
                } else {
                    val error = ""
                }
            })
    }

    fun setFilter(filter: String) {
        if(!RoomsMap.isFilter){
            if(filter.isBlank()){
                events.clear()
                events.addAll(fullEvents)
                RoomsMap.rooms = events
                viewState.setChats(events)
                viewState?.updatePinsOnMap(events)
            }else{
                val myFilter = filter.replace(",","").replace(".","").trim()
                val list = fullEvents.filter {
                    val myString = (it.name!! + " " + it.address).replace(",","").replace(".","")
                    myString.contains(myFilter, true)
                }
                events.clear()
                events.addAll(list)
                RoomsMap.rooms = events
                viewState.setChats(events)
                viewState?.updatePinsOnMap(events)
            }
        }
    }

    fun setDateFilter(date:String,calendar: Calendar?=null){
        /*
        val today = Calendar.getInstance()
        val filteredRooms = arrayListOf<RoomPin>()

        when (date) {
            "Сегодня" -> {
                for (i in RoomsMap.rooms.indices){
                    val time  = DateHolder(RoomsMap.rooms[i].eventStartDate!!)
                    if(time.dayOfMonth==today.get(Calendar.DAY_OF_MONTH)){
                        filteredRooms.add(RoomsMap.rooms[i])
                    }
                }
                RoomsMap.rooms = filteredRooms
                viewState.setChats(filteredRooms)
            }
            "Завтра" -> {
                val tomorrow = Calendar.getInstance()
                tomorrow.add(Calendar.DATE, 1)

                for (i in RoomsMap.rooms.indices){
                    val time  = DateHolder(RoomsMap.rooms[i].eventStartDate!!)
                    if(time.dayOfMonth==tomorrow.get(Calendar.DAY_OF_MONTH)){
                        filteredRooms.add(RoomsMap.rooms[i])
                    }
                }
                RoomsMap.rooms = filteredRooms
                viewState.setChats(filteredRooms)
            }
            "В выходные" -> {
                val saturday = Utils.getNextSaturdayDate()
                val sunday = Utils.getNextSundayDate()

                for (i in RoomsMap.rooms.indices){
                    val time  = DateHolder(RoomsMap.rooms[i].eventStartDate!!)
                    if(time.dayOfMonth==saturday.get(Calendar.DAY_OF_MONTH) || time.dayOfMonth==sunday.get(
                            Calendar.DAY_OF_MONTH)){
                        filteredRooms.add(RoomsMap.rooms[i])
                    }
                }
                RoomsMap.rooms = filteredRooms
                viewState.setChats(filteredRooms)
            }
            "Выбрать дату" -> {
                for (i in RoomsMap.rooms.indices){
                    val time  = DateHolder(RoomsMap.rooms[i].eventStartDate!!)
                    if(time.dayOfMonth==calendar!!.get(Calendar.DAY_OF_MONTH)){
                        filteredRooms.add(RoomsMap.rooms[i])
                    }
                }
                RoomsMap.rooms = filteredRooms
                viewState.setChats(filteredRooms)
            }
        }
        RoomsMap.isFilter = true
        RoomsMap.isFilter = true
         */
        viewState.setChats(RoomsMap.rooms)


    }
}