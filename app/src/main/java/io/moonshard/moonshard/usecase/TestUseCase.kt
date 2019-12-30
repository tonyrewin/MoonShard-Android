package io.moonshard.moonshard.usecase

import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.repository.NetworkRepository
import io.reactivex.Single
import okhttp3.ResponseBody
import javax.inject.Inject

class TestUseCase {

    @Inject
    internal lateinit var networkRepository: NetworkRepository

    init {
        MainApplication.getComponent().inject(this)
    }

}