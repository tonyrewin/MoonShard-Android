package io.moonshard.moonshard.models.api

import com.google.gson.annotations.SerializedName

data class Category(
    @SerializedName("id")
    var id: Int,
    @SerializedName("categoryName")
    var categoryName: String?)