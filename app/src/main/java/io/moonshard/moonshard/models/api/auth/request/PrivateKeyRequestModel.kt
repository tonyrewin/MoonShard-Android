package io.moonshard.moonshard.models.api.auth.request

import com.google.gson.annotations.SerializedName

data class PrivateKeyRequestModel(
    @SerializedName("privateKey")
    var privateKey: String?=null,
    @SerializedName("publicKey")
    var addressWallet: String?=null
)