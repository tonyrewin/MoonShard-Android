package io.moonshard.moonshard.presentation.presenter


import io.moonshard.moonshard.API
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.presentation.view.LoginView
import io.moonshard.moonshard.usecase.TestUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import javax.inject.Inject


@InjectViewState
class LoginPresenter : MvpPresenter<LoginView>() {

}