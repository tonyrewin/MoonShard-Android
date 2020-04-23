package io.moonshard.moonshard.models.api.auth.response

import com.google.gson.annotations.SerializedName

data class GeneralResponseAuth(@SerializedName("result")
                                var result: String)