package io.moonshard.moonshard.repository

import com.orhanobut.logger.Logger
import io.moonshard.moonshard.API
import io.moonshard.moonshard.MainApplication
import io.reactivex.Single
import okhttp3.ResponseBody
import java.io.File
import javax.inject.Inject



class NetworkRepository  {
    @Inject
    internal lateinit var api: API

    init {
        MainApplication.getComponent().inject(this)
    }

    fun getTest(): Single<ResponseBody> {
        return api.test()
    }
}