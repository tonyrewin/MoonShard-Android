package io.moonshard.moonshard.usecase

import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.models.api.RoomPin
import io.moonshard.moonshard.repository.RoomsRepository
import io.reactivex.Single
import javax.inject.Inject

class RoomsUseCase {

    @Inject
    internal lateinit var roomsRepository: RoomsRepository

    init {
        MainApplication.getComponent().inject(this)
    }

    fun putRoom(latitude: String, longitude: String, ttl: String, roomId: String,
        category: String
    ): Single<RoomPin> {
        return roomsRepository.putRoom(latitude, longitude, ttl, roomId, category)
    }


    fun getRooms(lat: String, lng: String, radius: String
    ): Single<ArrayList<RoomPin>> {
        return roomsRepository.getRooms(lat,lng,radius)
    }

    // fun getTest(): Single<ResponseBody> {
    //      return networkRepository.getTest()
    //  }
}