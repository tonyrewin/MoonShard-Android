package io.moonshard.moonshard.ui.activities

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.widget.TextView
import android.widget.Toast
import de.adorsys.android.securestoragelibrary.SecurePreferences
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.R
import io.moonshard.moonshard.presentation.presenter.RegisterPresenter
import io.moonshard.moonshard.presentation.view.RegisterView
import io.moonshard.moonshard.services.XMPPConnectionService
import kotlinx.android.synthetic.main.activity_register.*
import moxy.MvpAppCompatActivity
import moxy.presenter.InjectPresenter
import java.util.*

class RegisterActivity : MvpAppCompatActivity(), RegisterView {

    @InjectPresenter
    lateinit var presenter: RegisterPresenter

    private val timer = Timer(true)

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_register)
        startService()


        val alreadyHaveText: TextView = findViewById(R.id.alreadyHaveText)
        alreadyHaveText.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        registerBtn.setOnClickListener {
            // val connect = XMPPConnection(applicationContext)
            // MainApplication.setXmppConnection(connect)
            presenter.register(editEmail.text.toString(), editPassword.text.toString())
            //startService()
        }

      //  Handler().postDelayed({
        //    auth()
      //  }, 5000)
    }

    fun startService() {
        startService(Intent(applicationContext, XMPPConnectionService::class.java))
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
    }

    override fun showContactsScreen() {
        val intentContactsActivity = Intent(
            this,
            MainActivity::class.java
        )
        startActivity(intentContactsActivity)
    }

    fun auth() {
        val jid = SecurePreferences.getStringValue("jid", "")
        val pass = SecurePreferences.getStringValue("pass", "")
        val logged = SecurePreferences.getBooleanValue("logged_in", false)

        if (logged) {
            presenter.login(jid!!, pass!!)
        }
    }

    override fun showToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    override fun showLoader() {
    }

    override fun hideLoader() {

    }
}
