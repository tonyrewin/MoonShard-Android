package io.moonshard.moonshard.repository

import io.moonshard.moonshard.API
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.common.ApiConstants.Companion.COMPLEX_BASE_URL_UNIT_PAY
import io.moonshard.moonshard.common.ApiConstants.Companion.UNIT_PAY_CREATE_PAYMENT
import io.moonshard.moonshard.models.api.CreatePaymentRequestModel
import io.reactivex.Single
import javax.inject.Inject

class UnitPayRepository {
    @Inject
    internal lateinit var api: API

    init {
        MainApplication.getComponent().inject(this)
    }

    fun createPay(
        sum: Int, account: String, desc: String
    ): Single<okhttp3.Response> {
        val request = CreatePaymentRequestModel(account, sum, desc)
        return api.createPay("$COMPLEX_BASE_URL_UNIT_PAY/$UNIT_PAY_CREATE_PAYMENT", request)
    }
}