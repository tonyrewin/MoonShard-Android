package io.moonshard.moonshard.presentation.view.profile.wallet.transfer

import android.graphics.Bitmap
import moxy.MvpView

interface TransferWalletView: MvpView {
    fun showAvatarRecipient(avatar: Bitmap)
    fun setDataRecipient(name: String, status: String)
    fun showToast(text: String)
    fun back()
}