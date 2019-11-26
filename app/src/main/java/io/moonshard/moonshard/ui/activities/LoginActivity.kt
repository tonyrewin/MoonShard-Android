package io.moonshard.moonshard.ui.activities


import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.widget.Toast
import de.adorsys.android.securestoragelibrary.SecurePreferences
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.presentation.presenter.LoginPresenter
import io.moonshard.moonshard.presentation.view.LoginView
import io.moonshard.moonshard.services.XMPPConnection
import io.moonshard.moonshard.services.XMPPConnectionService
import kotlinx.android.synthetic.main.activity_login.*
import moxy.MvpAppCompatActivity
import moxy.presenter.InjectPresenter
import org.jivesoftware.smack.ConnectionListener
import java.util.*
import com.dropbox.core.v2.teamlog.ActorLogInfo.app
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import kotlin.Exception


class LoginActivity : BaseActivity(), LoginView {
    override fun onError(e: Exception) {
        runOnUiThread {
            hideLoader()
            e.message?.let { showError(it) }?:showError("Произошла ошибка")
        }
    }

    @InjectPresenter
    lateinit var presenter: LoginPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(io.moonshard.moonshard.R.layout.activity_login)

        //MainApplication.getXmppConnection().connection.addConnectionListener(this)

        loginBtn.setOnClickListener {
            showLoader()
            saveLoginCredentials(editEmail.text.toString(), editPassword.text.toString())
            startService()

            // presenter.login(editEmail.text.toString(), editPassword.text.toString())
        }

        dontHaveText.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
    }

    private fun startService() {
        startService(Intent(applicationContext, XMPPConnectionService::class.java))
        /*
        val connection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName, service: IBinder) {
                val binder = service as XMPPConnectionService.XMPPServiceBinder
                MainApplication.setXmppConnection(binder.connection)
            }

            override fun onServiceDisconnected(name: ComponentName) {
                MainApplication.setXmppConnection(null)
            }
        }
        MainApplication.setServiceConnection(connection)
        applicationContext.bindService(
            Intent(applicationContext, XMPPConnectionService::class.java),
            connection,
            Context.BIND_AUTO_CREATE
        )
         */

    }

    override fun createNewConnect() {
    }

    override fun onAuthenticated() {
        runOnUiThread {
            hideLoader()
            showContactsScreen()
        }
    }

    private fun saveLoginCredentials(email: String, password: String) {
        SecurePreferences.setValue("jid", email)
        SecurePreferences.setValue("pass", password)
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
