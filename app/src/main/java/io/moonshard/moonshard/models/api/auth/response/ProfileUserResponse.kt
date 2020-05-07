package io.moonshard.moonshard.models.api.auth.response

import com.google.gson.annotations.SerializedName

data class ProfileUserResponse(
    @SerializedName("email")
    var email: String? = null,
    @SerializedName("isActivated")
    var isActivated: Boolean? = null,
    @SerializedName("username")
    var username: String,
    @SerializedName("uuid")
    var uuid: String? = null
)