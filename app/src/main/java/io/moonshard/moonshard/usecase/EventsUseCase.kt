package io.moonshard.moonshard.usecase

import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.models.api.Category
import io.moonshard.moonshard.models.api.RoomPin
import io.moonshard.moonshard.models.api.tickets.TicketTypeName
import io.moonshard.moonshard.repository.EventsRepository
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

class EventsUseCase {

    @Inject
    internal lateinit var eventsRepository: EventsRepository

    init {
        MainApplication.getComponent().inject(this)
    }

    fun putRoom(
        latitude: Double?, longitude: Double?, ttl: Int, roomId: String,
        categories: ArrayList<Category>,
        idGroup: String?,
        eventStartDate: String,
        name:String,address:String
    ): Single<RoomPin> {
        return eventsRepository.putRoom(latitude, longitude, ttl, roomId, categories,idGroup,eventStartDate,name,address)
    }

    fun getRooms(
        lat: String, lng: String, radius: String
    ): Single<ArrayList<RoomPin>> {
        return eventsRepository.getRooms(lat, lng, radius)
    }

    fun getRoomsByCategory(categoryId: Int,lat: String, lng: String, radius: String):Single<ArrayList<RoomPin>>{
        return eventsRepository.getRoomsByCategory(categoryId,lat, lng,radius)
    }

    fun changeRoom(room:RoomPin):Single<RoomPin>{
        return eventsRepository.changeRoom(room)
    }

    fun getRoom(eventId:String):Single<RoomPin>{
        return eventsRepository.getRoom(eventId)
    }

    fun deleteRoom(eventId:String):Completable{
        return eventsRepository.deleteRoom(eventId)
    }

    fun createTicketTypeName(eventID:String,typeName:String,typeID:Int):Single<TicketTypeName>{
        return eventsRepository.createTicketTypeName(eventID,typeName,typeID)
    }

    fun getTicketTypeName(eventID:String,typeID:Int):Single<TicketTypeName>{
        return eventsRepository.getTicketTypeName(eventID,typeID)
    }
}