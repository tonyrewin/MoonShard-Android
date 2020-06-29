package io.moonshard.moonshard.models.wallet

import java.math.BigInteger

data class QrCodeModel(
    val ticketId: BigInteger,
    val addressWallet: String,
    val jidEvent: String,
    val ticketSaleAddress: String,
    val ticketPayState: BigInteger,
    val ticketType: BigInteger
)