package io.moonshard.moonshard.common.utils

import io.moonshard.moonshard.common.BasePresenter
import io.reactivex.disposables.Disposable
import moxy.MvpView

fun <V : MvpView?> Disposable.autoDispose(presenter: BasePresenter<V>) {
    presenter.addDisposable(this)
}