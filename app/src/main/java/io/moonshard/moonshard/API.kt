package io.moonshard.moonshard

import io.moonshard.moonshard.models.ModelMapRequest
import io.moonshard.moonshard.models.api.Category
import io.moonshard.moonshard.models.api.RoomPin
import io.reactivex.Single
import retrofit2.http.*


interface API {

    @GET("/rooms/getByCoords")
    fun getRooms(
        @Query("gps_lat") lat: String,
        @Query("gps_lon") lng: String,
        @Query("radius") radius: String
    ): Single<ArrayList<RoomPin>>

    @Headers("Accept: application/json", "Content-type:application/json")
    @POST("/rooms/put")
    fun putRoom(@Body request: ModelMapRequest): Single<RoomPin>

    @GET("/rooms/id")
    fun getRoomsById()

    @GET("/categories")
    fun getCategories(): Single<ArrayList<Category>>

    @GET("/rooms/byCategory/{categoryId}")
    fun getRoomsByCategory(
        @Path("categoryId") categoryId: Int,
        @Query("gps_lat") lat: String,
        @Query("gps_lon") lng: String,
        @Query("radius") radius: String
    ): Single<ArrayList<RoomPin>>

    @Headers("Accept: application/json", "Content-type:application/json")
    @PUT("/rooms/updateRooms")
    fun changeRoom(@Body room:RoomPin):Single<RoomPin>

    @GET("rooms/{eventId}")
    fun getRoom(@Path("eventId") eventId:Long):Single<RoomPin>
}