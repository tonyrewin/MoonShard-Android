package io.moonshard.moonshard.models.api.auth.response

import com.google.gson.annotations.SerializedName

data class TokenModelResponse(
    @SerializedName("accessToken")
    var accessToken: String,
    @SerializedName("refreshToken")
    var refreshToken: String
)