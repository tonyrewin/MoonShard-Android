package io.moonshard.moonshard

import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface API {
    /*
    @GET("getRooms")
    fun getRooms(
        @Query("gps_lat") lat: String,
        @Query("gps_lon") lng: String,
        @Query("radius") radius: String
    ): Observable<ArrayList<RoomPin>>
     */

    @POST("putRoom")
    fun putRoom(
        @Query("gps_lat") country: String,
        @Query("gps_lon") appId: String,
        @Query("roomID") roomID: String,
        @Query("ttl") ttl: String
    ): Observable<String>

    @DELETE("removeRoom")
    fun removeRoom(@Query("id") roomID: String):Observable<String>

    @GET("/api/v1/employees")
    fun test(): Single<ResponseBody>

}