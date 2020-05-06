package io.moonshard.moonshard.presentation.view.profile

import moxy.MvpView

interface VerificationEmailView: MvpView {
    fun showToast(text: String)
}