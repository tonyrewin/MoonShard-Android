package io.moonshard.moonshard.repository

import io.moonshard.moonshard.API
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.models.api.RoomPin
import io.reactivex.Single
import okhttp3.ResponseBody
import javax.inject.Inject

class RoomsRepository {

    @Inject
    internal lateinit var api: API

    init {
        MainApplication.getComponent().inject(this)
    }

    fun putRoom(latitude: String, longitude: String, ttl: String, roomId: String,
        category: String
    ): Single<RoomPin> {
        return api.putRoom(latitude,longitude,ttl,roomId,category)
    }


    fun getRooms(lat: String, lng: String, radius: String
    ): Single<ArrayList<RoomPin>> {
        return api.getRooms(lat,lng,radius)
    }
}