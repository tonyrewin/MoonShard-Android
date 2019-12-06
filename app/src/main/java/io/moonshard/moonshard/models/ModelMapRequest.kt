package io.moonshard.moonshard.models

import com.google.gson.annotations.SerializedName


data class ModelMapRequest (
    @SerializedName("latitude")
    private val latitude: Float?=null,
    @SerializedName("longtitude")
    private val longtitude: Float? = null,
    @SerializedName("ttl")
    private val ttl: Int? = null,
    @SerializedName("roomId")
    private val roomId: String? = null,
    @SerializedName("category")
    private val category: String? = null)
