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
        latitude: Double?, longitude: Double?, ttl: Int, roomId: String,
        categories: ArrayList<Category>,
        idGroup: String?,
        eventStartDate: Long,
        name:String,address:String
    ): Single<RoomPin> {
        return roomsRepository.putRoom(latitude, longitude, ttl, roomId, categories,idGroup,eventStartDate,name,address)
    }

    fun getRooms(
        lat: String, lng: String, radius: String
    ): Single<ArrayList<RoomPin>> {
        return roomsRepository.getRooms(lat, lng, radius)
    }

    fun getCategories(): Single<ArrayList<Category>> {
        return roomsRepository.getCategories()
    }

    fun getRoomsByCategory(categoryId: Int,lat: String, lng: String, radius: String):Single<ArrayList<RoomPin>>{
        return roomsRepository.getRoomsByCategory(categoryId,lat, lng,radius)
    }

    fun changeRoom(room:RoomPin):Single<RoomPin>{
        return roomsRepository.changeRoom(room)
    }

    fun getRoom(eventId:Long):Single<RoomPin>{
        return roomsRepository.getRoom(eventId)
    }

}