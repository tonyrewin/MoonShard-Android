package io.moonshard.moonshard.models.api.auth.request

import com.google.gson.annotations.SerializedName

data class RegisterRequestModel(@SerializedName("email")
                           var email: String,
                                @SerializedName("password")
                           var password: String,
                                @SerializedName("username")
                           var username: String)