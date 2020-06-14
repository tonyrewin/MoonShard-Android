package io.moonshard.moonshard

import io.moonshard.moonshard.models.ModelMapRequest
import io.moonshard.moonshard.models.api.CreatePaymentRequestModel
import io.moonshard.moonshard.models.api.RoomPin
import io.moonshard.moonshard.models.api.auth.request.*
import io.moonshard.moonshard.models.api.auth.response.*
import io.moonshard.moonshard.models.api.tickets.TicketTypeName
import io.moonshard.moonshard.models.api.tickets.TicketTypeNameRequest
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


    @GET("/rooms/byCategory/{categoryId}")
    fun getRoomsByCategory(
        @Path("categoryId") categoryId: Int,
        @Query("gps_lat") lat: String,
        @Query("gps_lon") lng: String,
        @Query("radius") radius: String
    ): Single<ArrayList<RoomPin>>

    @Headers("Accept: application/json", "Content-type:application/json")
    @PUT("/rooms/update")
    fun changeRoom(@Body room: RoomPin): Single<RoomPin>

    @GET("rooms/{eventId}")
    fun getRoom(@Path("eventId") eventId: String): Single<RoomPin>

    @DELETE("rooms/{eventId}")
    fun deleteRoom(@Path("eventId") eventId: String): Completable

    @Headers("Accept: application/json", "Content-type:application/json")
    @POST
    fun resetPassword(
        @Url url: String,
        @Body request: RecoveryPassRequestModel
    ): Single<GeneralResponseAuth>

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
    fun savePrivateKey(
        @Url url: String,
        @Body request: PrivateKeyRequestModel,
        @Header("Authorization") authHeader: String
    ): Single<GeneralResponseAuth>

    @Headers("Accept: application/json", "Content-type:application/json")
    @GET
    fun getPrivateKey(
        @Url url: String,
        @Header("Authorization") authHeader: String
    ): Single<PrivateKeyAuthResponse>

    @Headers("Accept: application/json", "Content-type:application/json")
    @GET
    fun getPublicKey(
        @Url url: String,
        @Query("username") username: String,
        @Header("Authorization") authHeader: String
    ): Single<PublicKeyAuthResponse>

    @Headers("Accept: application/json", "Content-type:application/json")
    @POST
    fun addEmailToProfile(
        @Url url: String,
        @Body request: EmailToProfileRequestModel,
        @Header("Authorization") authHeader: String
    ): Single<GeneralResponseAuth>

    @Headers("Accept: application/json", "Content-type:application/json")
    @GET
    fun getUserProfileInfo(
        @Url url: String,
        @Header("Authorization") authHeader: String
    ): Single<ProfileUserResponse>

    @Headers("Accept: application/json", "Content-type: application/json")
    @POST
    fun createPay(
        @Url url: String,
        @Body request: CreatePaymentRequestModel
    ): Single<Response>

    @Headers("Accept: application/json", "Content-type:application/json")
    @POST("/ticketTypeNames")
    fun createTicketTypeName(@Body request: TicketTypeNameRequest): Single<TicketTypeName>

    @Headers("Accept: application/json", "Content-type:application/json")
    @GET("/ticketTypeNames")
    fun getTicketTypeName(
        @Query("eventID") eventID: String, @Query("typeID") typeID: Int)
            : Single<TicketTypeName>

    @Headers("Accept: application/json", "Content-type:application/json")
    @PUT("/ticketTypeNames")
    fun updateTicketTypeName(
        @Url url: String,
        @Body request: TicketTypeNameRequest
    ): Single<TicketTypeName>


    @Headers("Accept: application/json", "Content-type:application/json")
    @DELETE("/ticketTypeNames")
    fun deleteTicketTypeName(@Url url: String, @Body request: TicketTypeNameRequest): Completable
}