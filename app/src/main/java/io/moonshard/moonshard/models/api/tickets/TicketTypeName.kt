package io.moonshard.moonshard.models.api.tickets

import com.google.gson.annotations.SerializedName

data class TicketTypeName(
    @SerializedName("eventID")
    var eventID: String,
    @SerializedName("typeName")
    var typeName: String,
    @SerializedName("typeID")
    var typeID: Int,
    @SerializedName("id")
    var id: String
)