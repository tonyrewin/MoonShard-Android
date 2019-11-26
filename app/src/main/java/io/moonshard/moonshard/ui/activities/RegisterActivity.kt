package io.moonshard.moonshard.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import de.adorsys.android.securestoragelibrary.SecurePreferences
import io.moonshard.moonshard.R
import io.moonshard.moonshard.presentation.presenter.RegisterPresenter
import io.moonshard.moonshard.presentation.view.RegisterView
import io.moonshard.moonshard.services.XMPPConnectionService
import kotlinx.android.synthetic.main.activity_register.*
import moxy.presenter.InjectPresenter
import java.util.*

class RegisterActivity : BaseActivity(), RegisterView {

    @InjectPresenter
    lateinit var presenter: RegisterPresenter

    private val timer = Timer(true)

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        startService()
        auth()
        alreadyHaveText?.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        registerBtn?.setOnClickListener {
            presenter.register(editEmail.text.toString(), editPassword.text.toString())
            //startService()
        }
    }

    override fun onSuccess() {
        runOnUiThread {
            hideLoader()
            Toast.makeText(this, "Registration is success", Toast.LENGTH_SHORT).show()
        }
    }

    fun startService() {
        startService(Intent(applicationContext, XMPPConnectionService::class.java))
        /*
        val connection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName, service: IBinder) {
                val binder = service as XMPPConnectionService.XMPPServiceBinder
                MainApplication.setXmppConnection(binder.connection)
                    //if(MainApplication.getXmppConnection()!=null) auth()
            }

            override fun onServiceDisconnected(name: ComponentName) {
               // MainApplication.setXmppConnection(null)
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

    override fun showError(error: String) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
    }

    override fun onError(e: Exception) {
        runOnUiThread {
            hideLoader()
            e.message?.let { showError(it) } ?: showError("Произошла ошибка")
        }
    }

    override fun showContactsScreen() {
        val intentContactsActivity = Intent(
            this,
            MainActivity::class.java
        )
        startActivity(intentContactsActivity)
    }

    private fun auth() {
        val logged = SecurePreferences.getBooleanValue("logged_in", false)
        if (logged) {
            showContactsScreen()
        } else {
            setContentView(R.layout.activity_register)
        }
    }

    override fun onAuthenticated() {
        //showContactsScreen()
    }

    override fun showToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    override fun showLoader() {
        progressBarReg?.visibility = View.VISIBLE
    }

    override fun hideLoader() {
        progressBarReg?.visibility = View.GONE
    }
}
