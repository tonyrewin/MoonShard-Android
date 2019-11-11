package io.moonshard.moonshard.presentation.presenter

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.helpers.AppHelper
import io.moonshard.moonshard.presentation.view.RegisterView
import io.moonshard.moonshard.services.XMPPConnection
import org.jivesoftware.smack.SmackException
import org.jivesoftware.smack.XMPPException



@InjectViewState
class RegisterPresenter : MvpPresenter<RegisterView>() {

     fun register(email: String, pass: String) {
         viewState.showLoader()
        try {
            AppHelper.getXmppConnection()?.register(email, pass)
            viewState.showToast("Register is success")
           // return true
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
     //   return false
    }
}