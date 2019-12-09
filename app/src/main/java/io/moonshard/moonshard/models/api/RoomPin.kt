package io.moonshard.moonshard.models.api

import com.google.gson.annotations.SerializedName

data class RoomPin(@SerializedName("id")
                   var id: String,
                   @SerializedName("latitude")
                   var latitude: String,
                   @SerializedName("longitude")
                   var longitude: String,
                   @SerializedName("created_at")
                   var createdAt: Boolean,
                   @SerializedName("ttl")
                   var ttl: String?,
                   @SerializedName("categories")
                   var category: ArrayList<Category>?,
                   @SerializedName("roomId")
                   var roomId: String?)