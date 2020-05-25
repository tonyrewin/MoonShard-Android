package io.moonshard.moonshard.models.api.auth.request

import com.google.gson.annotations.SerializedName

data class EmailToProfileRequestModel(@SerializedName("email")
            var email: String)