package io.moonshard.moonshard.models.api.auth.response

import com.google.gson.annotations.SerializedName

data class PublicKeyAuthResponse(@SerializedName("result")
                            var walletAddress: String)