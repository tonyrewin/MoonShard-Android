package io.moonshard.moonshard.models.api

import com.google.gson.annotations.SerializedName

data class RoomPin(@SerializedName("id")
                   var id: Long,
                   @SerializedName("latitude")
                   var latitude: Double,
                   @SerializedName("longitude")
                   var longitude: Double,
                   @SerializedName("created_at")
                   var createdAt: Long,
                   @SerializedName("ttl")
                   var ttl: Long?,
                   @SerializedName("categories")
                   var category: ArrayList<Category>?,
                   @SerializedName("roomId")
                   var roomId: String?,
                   @SerializedName("parentGroupId")
                   var parentGroupId: String?,
                   @SerializedName("eventStartDate")
                   var eventStartDate:Long?,
                   @SerializedName("name")
                   var name:String?,
                   @SerializedName("address")
                   var address:String?)