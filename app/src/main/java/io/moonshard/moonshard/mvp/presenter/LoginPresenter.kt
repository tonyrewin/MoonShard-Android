package io.moonshard.moonshard.mvp.presenter

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import io.moonshard.moonshard.API
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.mvp.view.LoginView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject


@InjectViewState
class LoginPresenter : MvpPresenter<LoginView>() {

    @Inject
    internal lateinit var api: API

    init {
        MainApplication.getComponent().inject(this)
    }

    fun login(homeserverUri: String, identityUri: String, email: String, password: String) {
        viewState?.showLoader()
/*
        api.test().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe( { result->
                var kek = ""
                viewState?.hideLoader()
            },{
                var kek = ""
                viewState?.hideLoader()
            })
*/
    }
}