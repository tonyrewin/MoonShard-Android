package io.moonshard.moonshard.models.api.auth.request

import com.google.gson.annotations.SerializedName

data class RecoveryPassRequestModel(
    @SerializedName("email")
    var email: String,
    @SerializedName("newPassword")
    var newPassword: String
)