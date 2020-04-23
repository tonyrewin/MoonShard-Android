package io.moonshard.moonshard.models.api.auth.response

import com.google.gson.annotations.SerializedName

data class PrivateKeyAuthResponse( @SerializedName("privateKey")
                                   var privateKey: String)