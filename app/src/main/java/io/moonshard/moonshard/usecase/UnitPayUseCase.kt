package io.moonshard.moonshard.usecase

import io.moonshard.moonshard.repository.UnitPayRepository
import io.reactivex.Single
import okhttp3.Response
import javax.inject.Inject

class UnitPayUseCase {

    @Inject
    internal lateinit var repository: UnitPayRepository

    fun createPay(
        sum: Int,account:String,desc:String
    ): Single<Response> {
        return repository.createPay(sum, account,desc)
    }
}