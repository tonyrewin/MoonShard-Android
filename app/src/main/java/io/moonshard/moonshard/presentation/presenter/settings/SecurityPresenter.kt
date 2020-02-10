package io.moonshard.moonshard.presentation.presenter.settings

import de.adorsys.android.securestoragelibrary.SecurePreferences
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.presentation.view.settings.SecurityView
import moxy.InjectViewState
import moxy.MvpPresenter
import org.jivesoftware.smackx.iqregister.AccountManager

@InjectViewState
class SecurityPresenter : MvpPresenter<SecurityView>() {


    fun changePassword(
        newPass: String, repeatNewPass: String, currentPass: String
    ) {
        if (newPass == repeatNewPass) {
            changeNewPassword(newPass)
        } else {
            viewState?.showError("Пароли не совпадают")
        }

        /*
        if (SecurePreferences.getStringValue("pass", "") == currentPass) {
            if (newPass == repeatNewPass) {
                changeNewPassword(newPass)
            } else {
                viewState?.showError("Пароли не совпадают")
            }
        } else {
            viewState?.showError("Текущий пароль неверный")
        }
         */
    }

    private fun changeNewPassword(newPass: String) {
        try {
            val am = AccountManager.getInstance(MainApplication.getXmppConnection().connection)
            am.changePassword(newPass)
            viewState?.showSettingsScreen()
        } catch (e: Exception) {
            viewState?.showError(e.message.toString())
        }
    }
}