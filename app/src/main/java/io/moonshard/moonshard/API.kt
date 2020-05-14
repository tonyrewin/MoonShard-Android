package io.moonshard.moonshard

import io.moonshard.moonshard.models.ModelMapRequest
import io.moonshard.moonshard.models.api.Category
import io.moonshard.moonshard.models.api.RoomPin
import io.moonshard.moonshard.models.api.auth.request.*
import io.moonshard.moonshard.models.api.auth.response.GeneralResponseAuth
import io.moonshard.moonshard.models.api.auth.response.PrivateKeyAuthResponse
import io.moonshard.moonshard.models.api.auth.response.ProfileUserResponse
import io.moonshard.moonshard.models.api.auth.response.TokenModelResponse
import io.reactivex.Completable
import io.reactivex.Single
import okhttp3.Response
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
    fun changeRoom(@Body room: RoomPin): Single<RoomPin>

    @GET("rooms/{eventId}")
    fun getRoom(@Path("eventId") eventId: Long): Single<RoomPin>

    @DELETE("rooms/{eventId}")
    fun deleteRoom(@Path("eventId") eventId: String): Completable

    @Headers("Accept: application/json", "Content-type:application/json")
    @POST
    fun resetPassword(@Url url: String, @Body request: RecoveryPassRequestModel): Single<GeneralResponseAuth>

    @Headers("Accept: application/json", "Content-type:application/json")
    @POST
    fun login(@Url url: String, @Body request: LoginRequestModel): Single<TokenModelResponse>

    @Headers("Accept: application/json", "Content-type:application/json")
    @POST
    fun register(@Url url: String, @Body request: RegisterRequestModel): Single<GeneralResponseAuth>

    @Headers("Accept: application/json", "Content-type:application/json")
    @POST
    fun logout(@Url url: String, @Body request: TokenRequestModel): Single<GeneralResponseAuth>

    @Headers("Accept: application/json", "Content-type:application/json")
    @POST
    fun refreshToken(@Url url: String, @Body request: TokenRequestModel): Single<TokenModelResponse>

    @Headers("Accept: application/json", "Content-type:application/json")
    @POST
    fun savePrivateKey(@Url url: String, @Body request: PrivateKeyRequestModel, @Header("Authorization") authHeader: String): Single<GeneralResponseAuth>

    @Headers("Accept: application/json", "Content-type:application/json")
    @POST
    fun getPrivateKey(@Url url: String, @Body request: PrivateKeyRequestModel, @Header("Authorization") authHeader: String): Single<PrivateKeyAuthResponse>

    @Headers("Accept: application/json", "Content-type:application/json")
    @POST
    fun addEmailToProfile(@Url url: String, @Body request: EmailToProfileRequestModel, @Header("Authorization") authHeader: String): Single<GeneralResponseAuth>

    @Headers("Accept: application/json", "Content-type:application/json")
    @GET
    fun getUserProfileInfo(@Url url: String, @Header("Authorization") authHeader: String): Single<ProfileUserResponse>

    @Headers("Accept: application/json", "Content-type:application/json")
    @GET
    fun createPay(
        @Url url: String,
        @Query("sum") sum: Int,
        @Query("account") account: String,
        @Query("desc") desc: String
    ): Single<Response>
}