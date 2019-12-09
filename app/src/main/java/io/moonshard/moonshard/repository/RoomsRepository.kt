package io.moonshard.moonshard.repository

import com.google.gson.Gson
import io.moonshard.moonshard.API
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.models.ModelMapRequest
import io.moonshard.moonshard.models.api.Category
import io.moonshard.moonshard.models.api.RoomPin
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.http.Field
import javax.inject.Inject
import kotlin.math.ln

class RoomsRepository {

    @Inject
    internal lateinit var api: API

    init {
        MainApplication.getComponent().inject(this)
    }

    fun putRoom(latitude: Float, longitude: Float, ttl: Int, roomId: String,
        categories: ArrayList<Category>
    ): Single<RoomPin> {
        val request = ModelMapRequest(latitude,longitude,ttl,roomId,categories)
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

}