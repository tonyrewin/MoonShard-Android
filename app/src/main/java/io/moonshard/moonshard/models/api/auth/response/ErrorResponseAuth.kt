package io.moonshard.moonshard.models.api.auth.response

import com.google.gson.annotations.SerializedName

data class Error(@SerializedName("message")
                               var message: String)

data class ErrorResponse(@SerializedName("error")
                 var error: Error)