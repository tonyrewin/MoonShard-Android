package io.moonshard.moonshard.models.api.auth.request

import com.google.gson.annotations.SerializedName

data class PrivateKeyRequestModel(
    @SerializedName("encryptionPassword")
    var encryptionPassword: String
)