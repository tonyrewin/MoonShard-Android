package io.moonshard.moonshard.models.api.tickets

import com.google.gson.annotations.SerializedName

data class TicketTypeNameRequest( @SerializedName("eventID")
                                   var eventID:  String,
                                   @SerializedName("typeName")
                                   var typeName: String,
                                   @SerializedName("typeID")
                                   var typeID: Int)