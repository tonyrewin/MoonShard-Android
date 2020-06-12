package io.moonshard.moonshard.models.api

import com.google.gson.annotations.SerializedName

data class RoomPin(
    @SerializedName("id")
    var id: String,
    @SerializedName("latitude")
    var latitude: Double,
    @SerializedName("longitude")
    var longitude: Double,
    @SerializedName("created_at")
    var createdAt: String,
    @SerializedName("ttl")
    var ttl: Long?,
    @SerializedName("categories")
    var category: ArrayList<Category>?,
    @SerializedName("roomID")
    var roomID: String?,
    @SerializedName("parentGroupId")
    var parentGroupId: String?,
    @SerializedName("eventStartDate")
    var eventStartDate: String?,
    @SerializedName("name")
    var name: String?,
    @SerializedName("address")
    var address: String?,
    @SerializedName("expiresAt")
    var expiresAt: String?,
    var description:String
)