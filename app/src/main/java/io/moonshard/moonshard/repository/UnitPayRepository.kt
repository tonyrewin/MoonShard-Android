package io.moonshard.moonshard.repository

import io.moonshard.moonshard.API
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.common.ApiConstants.Companion.COMPLEX_BASE_URL_UNIT_PAY
import io.moonshard.moonshard.common.ApiConstants.Companion.UNIT_PAY_PAY
import io.reactivex.Single
import javax.inject.Inject

class UnitPayRepository {
    @Inject
    internal lateinit var api: API

    init {
        MainApplication.getComponent().inject(this)
    }

    fun createPay(
        sum: Int,account:String,desc:String
    ): Single<okhttp3.Response> {
        return api.createPay("$COMPLEX_BASE_URL_UNIT_PAY$UNIT_PAY_PAY/public_key", sum, account,desc)
    }
}