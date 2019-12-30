package io.moonshard.moonshard.repository

import io.moonshard.moonshard.API
import io.moonshard.moonshard.MainApplication
import io.reactivex.Single
import okhttp3.ResponseBody
import javax.inject.Inject


class NetworkRepository  {
    @Inject
    internal lateinit var api: API

    init {
        MainApplication.getComponent().inject(this)
    }


}