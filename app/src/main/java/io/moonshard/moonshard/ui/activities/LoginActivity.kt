package io.moonshard.moonshard.ui.activities


import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.widget.Toast
import com.arellomobile.mvp.MvpActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.presentation.presenter.LoginPresenter
import io.moonshard.moonshard.presentation.view.LoginView
import io.moonshard.moonshard.services.XMPPConnectionService
import kotlinx.android.synthetic.main.activity_login.*
import java.util.*


class LoginActivity : MvpActivity(), LoginView {

    private val timer = Timer(true)


    override fun test() {
        //val matrixInstance = Matrix.getInstance(applicationContext)
    }

    @InjectPresenter
    lateinit var presenter: LoginPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(io.moonshard.moonshard.R.layout.activity_login)

        loginBtn.setOnClickListener {
            /*
            presenter.login(
                "https://matrix.moonshard.tech", "https://vector.im",
                editEmail.text.toString(), editPassword.text.toString()
            )
             */
            doLogin()
        }

        dontHaveText.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun doLogin() {
        showLoader()
        startService(Intent(this, XMPPConnectionService::class.java))
        MainApplication.setServiceConnection(object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName, service: IBinder) {
                val binder = service as XMPPConnectionService.XMPPServiceBinder
                MainApplication.setXmppConnection(binder.connection)
            }

            override fun onServiceDisconnected(name: ComponentName) {
                MainApplication.setXmppConnection(null)
            }
        })
        bindService(
            Intent(this, XMPPConnectionService::class.java),
            MainApplication.getServiceConnection(),
            Context.BIND_AUTO_CREATE
        )
        timer.schedule(object : TimerTask() {
            override fun run() {
                if (MainApplication.getXmppConnection() != null) {
                    if (MainApplication.getXmppConnection().isConnectionAlive) {
                        showContactsScreen()
                        runOnUiThread {
                            hideLoader()
                        }
                        //EventBus.getDefault().post(AuthenticationStatusEvent(AuthenticationStatusEvent.CONNECT_AND_LOGIN_SUCCESSFUL))
                    } else {
                        runOnUiThread {
                            hideLoader()
                            showError("We have a incorrect login or password")
                        }
                        //  EventBus.getDefault().post(AuthenticationStatusEvent(AuthenticationStatusEvent.INCORRECT_LOGIN_OR_PASSWORD))
                    }
                } else {
                    runOnUiThread {
                        hideLoader()
                        showError("We have a error with internet")
                    }

                    //EventBus.getDefault().post(AuthenticationStatusEvent(AuthenticationStatusEvent.NETWORK_ERROR))
                }
            }
        }, 5000)
    }

    override fun showContactsScreen() {
        val intentContactsActivity = Intent(
            this,
            MainActivity::class.java
        )
        startActivity(intentContactsActivity)
    }

    override fun showLoader() {
        progressBarLogin.visibility = View.VISIBLE
    }

    override fun hideLoader() {
        progressBarLogin.visibility = View.GONE
    }

    override fun showError(error: String) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
    }
}
