package io.moonshard.moonshard.common.utils

import io.moonshard.moonshard.common.AutoDisposable
import io.reactivex.disposables.Disposable

fun Disposable.addTo(autoDisposable: AutoDisposable) {
    autoDisposable.add(this)
}