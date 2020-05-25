package io.moonshard.moonshard.models

import com.google.gson.annotations.SerializedName
import io.moonshard.moonshard.models.api.Category


data class ModelMapRequest(
    @SerializedName("latitude")
    private val latitude: Double? = null,
    @SerializedName("longitude")
    private val longitude: Double? = null,
    @SerializedName("ttl")
    private val ttl: Int? = null,
    @SerializedName("roomId")
    private val roomId: String? = null,
    @SerializedName("categories")
    private val category: ArrayList<Category>? = null,
    @SerializedName("parentGroupId")
    private val groupdId: String? = null,
    @SerializedName("eventStartDate")
    var eventStartDate:Long?=null,
    @SerializedName("name")
    var name:String?=null,
    @SerializedName("address")
    var address:String?=null
)
