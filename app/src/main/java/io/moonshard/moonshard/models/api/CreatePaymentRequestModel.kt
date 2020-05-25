package io.moonshard.moonshard.models.api

import com.google.gson.annotations.SerializedName

data class CreatePaymentRequestModel (
    @SerializedName("account")
    val account: String,
    @SerializedName("sum")
    val sum: Int,
    @SerializedName("desc")
    val desc: String
)