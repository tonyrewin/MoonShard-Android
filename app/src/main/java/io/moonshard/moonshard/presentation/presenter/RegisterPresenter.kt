package io.moonshard.moonshard.presentation.presenter


import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.helpers.AppHelper
import io.moonshard.moonshard.presentation.view.RegisterView
import io.moonshard.moonshard.services.XMPPConnection
import moxy.InjectViewState
import moxy.MvpPresenter
import org.jivesoftware.smack.SmackException
import org.jivesoftware.smack.XMPPException



@InjectViewState
class RegisterPresenter : MvpPresenter<RegisterView>() {

     fun register(email: String, pass: String) {
         viewState.showLoader()
        try {
            MainApplication.getXmppConnection()?.register(email, pass)
            viewState.showToast("Register is success")
        } catch (localXMPPException: XMPPException) {
            viewState.hideLoader()
            viewState.showToast(localXMPPException.message.toString())
            localXMPPException.printStackTrace()
        } catch (e: SmackException.NoResponseException) {
            viewState.hideLoader()
            viewState.showToast(e.message.toString())
            e.printStackTrace()
        } catch (e: SmackException.NotConnectedException) {
            viewState.hideLoader()
            viewState.showToast(e.message.toString())
            e.printStackTrace()
        }
    }

    fun login(email: String, password: String){
        val success = MainApplication.getXmppConnection().login(
            email,
            password
        )
        if (success) {
            viewState?.showContactsScreen()
        } else {
            AppHelper.resetLoginCredentials()
        }
    }


}