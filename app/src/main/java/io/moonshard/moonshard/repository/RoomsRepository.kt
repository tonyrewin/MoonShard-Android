package io.moonshard.moonshard.repository

import io.moonshard.moonshard.API
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.models.ModelMapRequest
import io.moonshard.moonshard.models.api.Category
import io.moonshard.moonshard.models.api.RoomPin
import io.reactivex.Single
import javax.inject.Inject

class RoomsRepository {

    @Inject
    internal lateinit var api: API

    init {
        MainApplication.getComponent().inject(this)
    }

    fun putRoom(
        latitude: Double?, longitude: Double?, ttl: Int, roomId: String,
        categories: ArrayList<Category>,
        idGroup: String?,
        eventStartDate: Long
    ): Single<RoomPin> {
        val request = ModelMapRequest(latitude,longitude,ttl,roomId,categories,idGroup,eventStartDate)
        return api.putRoom(request)
    }

    fun getRooms(lat: String, lng: String, radius: String
    ): Single<ArrayList<RoomPin>> {
        return api.getRooms(lat,lng,radius)
    }

    fun getCategories():Single<ArrayList<Category>>{
        return api.getCategories()
    }

    fun getRoomsByCategory(categoryId: Int,lat: String, lng: String, radius: String):Single<ArrayList<RoomPin>>{
        return api.getRoomsByCategory(categoryId,lat, lng,radius)
    }

    fun changeRoom(room:RoomPin):Single<RoomPin>{
        return api.changeRoom(room)
    }

    fun getRoom(eventId:Long):Single<RoomPin>{
        return api.getRoom(eventId)
    }
}