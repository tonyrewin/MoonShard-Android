package io.moonshard.moonshard.usecase

import io.moonshard.moonshard.repository.UnitPayRepository
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class UnitPayUseCase {
    /**
     * Create the payment
     * @return URL to open in user's browser
     */
    fun createPay(
        sum: Int, account: String, desc: String
    ): Single<String> {
        return Single.create<String> {
            UnitPayRepository.createPay(sum, account, desc)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ res ->
                    it.onSuccess(res.headers["Location"]!!)
                }, { ex ->
                    it.onError(ex)
                })
        }
    }
}