package io.moonshard.moonshard.usecase

import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.models.api.Category
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

    fun putRoom(
        latitude: Float, longitude: Float, ttl: Int, roomId: String,
        categories: ArrayList<Category>
    ): Single<RoomPin> {
        return roomsRepository.putRoom(latitude, longitude, ttl, roomId, categories)
    }

    fun getRooms(
        lat: String, lng: String, radius: String
    ): Single<ArrayList<RoomPin>> {
        return roomsRepository.getRooms(lat, lng, radius)
    }

    fun getCategories(): Single<ArrayList<Category>> {
        return roomsRepository.getCategories()
    }
}