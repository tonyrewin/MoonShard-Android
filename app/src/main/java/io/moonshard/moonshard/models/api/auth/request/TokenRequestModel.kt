package io.moonshard.moonshard.models.api.auth.request

import com.google.gson.annotations.SerializedName

data class TokenRequestModel(@SerializedName("accessToken")
                                 var accessToken: String,
                             @SerializedName("refreshToken")
                                 var refreshToken: String)