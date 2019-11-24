package io.moonshard.moonshard

import io.moonshard.moonshard.models.api.RoomPin
import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.http.*

interface API {

    @GET("/rooms/getByCoords")
    fun getRooms(
        @Query("gps_lat") lat: String,
        @Query("gps_lon") lng: String,
        @Query("radius") radius: String
    ): Single<ArrayList<RoomPin>>

    @FormUrlEncoded
    @PUT("/rooms/put")
    fun putRoom(
        @Field("latitude") latitude: String,
        @Field("longtitude") longitude: String,
        @Field("ttl") ttl: String,
        @Field("roomId") roomID: String,
        @Field("category") category: String
    ): Single<RoomPin>

    @GET("/rooms/id")
    fun getRoomsById()

    @GET("/api/v1/employees")
    fun test(): Single<ResponseBody>

}