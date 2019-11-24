package io.moonshard.moonshard.models.api

import com.google.gson.annotations.SerializedName

data class RoomPin(@SerializedName("id")
                   var id: String,
                   @SerializedName("latitude")
                   var latitude: String,
                   @SerializedName("longtitude")
                   var longtitude: String,
                   @SerializedName("created_at")
                   var createdAt: Boolean,
                   @SerializedName("ttl")
                   var ttl: String?,
                   @SerializedName("category")
                   var category: String?,
                   @SerializedName("roomId")
                   var roomId: String?)