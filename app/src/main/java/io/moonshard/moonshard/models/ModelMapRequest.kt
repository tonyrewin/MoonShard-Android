package io.moonshard.moonshard.models

import com.google.gson.annotations.SerializedName
import io.moonshard.moonshard.models.api.Category


data class ModelMapRequest (
    @SerializedName("latitude")
    private val latitude: Float?=null,
    @SerializedName("longitude")
    private val longitude: Float? = null,
    @SerializedName("ttl")
    private val ttl: Int? = null,
    @SerializedName("roomId")
    private val roomId: String? = null,
    @SerializedName("categories")
    private val category: ArrayList<Category>? = null)
