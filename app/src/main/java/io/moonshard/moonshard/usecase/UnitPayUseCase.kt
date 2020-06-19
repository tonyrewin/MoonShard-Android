package io.moonshard.moonshard.usecase

import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.repository.UnitPayRepository
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import javax.inject.Inject

class UnitPayUseCase {

    @Inject
    internal lateinit var repository: UnitPayRepository

    init {
        MainApplication.getComponent().inject(this)
    }

    /**
     * Create the payment
     * @return URL to open in user's browser
     */
    fun createPay(
        sum: Int, account: String, desc: String
    ): Single<String> {
        return Single.create<String> {
            repository.createPay(sum, account, desc)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ res ->
                    var kek = ""
                    //it.onSuccess(res.headers["Location"]!!)
                }, { ex ->
                    if((ex as HttpException).code() == 303){
                        val url = ex.response()?.headers()?.get("Location")
                        it.onSuccess(url!!)
                    }else{
                        it.onError(ex)

                    }
                })
        }
    }
}