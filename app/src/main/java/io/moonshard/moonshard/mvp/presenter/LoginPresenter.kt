package io.moonshard.moonshard.mvp.presenter

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import io.moonshard.moonshard.mvp.view.LoginView
import android.R.attr.password
import android.net.Uri
import io.moonshard.moonshard.common.matrix.LoginHandler
import org.matrix.androidsdk.HomeServerConnectionConfig
import org.matrix.androidsdk.core.callback.ApiCallback
import org.matrix.androidsdk.core.model.MatrixError
import org.matrix.androidsdk.ssl.CertUtil




@InjectViewState
class LoginPresenter : MvpPresenter<LoginView>() {

    fun showLoader() {
        viewState?.showLoader()
    }


    fun login(homeserverUri:String,identityUri:String,email:String,password:String){
        val hsConfig = HomeServerConnectionConfig.Builder()
            .withHomeServerUri(Uri.parse(homeserverUri))
            .withIdentityServerUri(Uri.parse(identityUri))
            .withShouldAcceptTlsExtensions(true)
            .build()

        val loginHandler = LoginHandler()
        /*
        loginHandler.login(
            getReactApplicationContext(),
            hsConfig,
            email,
            "",
            "",
            password,
            object : ApiCallback<Void> {
               override fun onNetworkError(e: Exception) {
                    val uException = CertUtil.getCertificateException(e)
                    if (uException != null) {
                        hsConfig.getAllowedFingerprints().add(uException!!.getFingerprint())
                        //login(hsConfig, email, password, promise)
                        return
                    }
                }

               override fun onMatrixError(e: MatrixError) {

                }

                override fun onUnexpectedError(e: Exception) {

                }

                override fun onSuccess(info: Void) {
                  //  onLoginSucceed()
                }
            })

         */
    }
}