package io.moonshard.moonshard.repository

import io.moonshard.moonshard.API
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.common.ApiConstants
import io.moonshard.moonshard.models.ModelMapRequest
import io.moonshard.moonshard.models.api.Category
import io.moonshard.moonshard.models.api.RoomPin
import io.moonshard.moonshard.models.api.tickets.TicketTypeName
import io.moonshard.moonshard.models.api.tickets.TicketTypeNameRequest
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

class EventsRepository {

    @Inject
    internal lateinit var api: API

    init {
        MainApplication.getComponent().inject(this)
    }

    fun putRoom(
        latitude: Double?, longitude: Double?, ttl: Int, roomId: String,
        categories: ArrayList<Category>,
        idGroup: String?,
        eventStartDate: String,
        name: String,address:String
    ): Single<RoomPin> {
        val request = ModelMapRequest(
            latitude,
            longitude,
            ttl,
            roomId,
            categories,
            idGroup,
            eventStartDate,
            name,
            address
        )
        return api.putRoom(request)
    }

    fun getRooms(
        lat: String, lng: String, radius: String
    ): Single<ArrayList<RoomPin>> {
        return api.getRooms(lat, lng, radius)
    }

    fun getRoomsByCategory(
        categoryId: Int,
        lat: String,
        lng: String,
        radius: String
    ): Single<ArrayList<RoomPin>> {
        return api.getRoomsByCategory(categoryId, lat, lng, radius)
    }

    fun changeRoom(room: RoomPin): Single<RoomPin> {
        return api.changeRoom(room)
    }

    fun getRoom(eventId: String): Single<RoomPin> {
        return api.getRoom(eventId)
    }

    fun deleteRoom(eventId: String):Completable{
        return api.deleteRoom(eventId)
    }

    fun createTicketTypeName(eventID:String,typeName:String,typeID:Int):Single<TicketTypeName>{
        val model = TicketTypeNameRequest(eventID,typeName,typeID)
        return api.createTicketTypeName(model)
    }

    fun getTicketTypeName(eventID:String,typeID:Int):Single<TicketTypeName>{
        return api.getTicketTypeName(eventID,typeID)
    }
}