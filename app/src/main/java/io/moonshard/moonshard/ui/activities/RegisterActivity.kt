package io.moonshard.moonshard.ui.activities

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.widget.TextView
import android.widget.Toast
import io.moonshard.moonshard.R
import io.moonshard.moonshard.helpers.AppHelper
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
    }

    fun startService() {
        startService(Intent(applicationContext, XMPPConnectionService::class.java))
        val connection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName, service: IBinder) {
                val binder = service as XMPPConnectionService.XMPPServiceBinder
                AppHelper.setXmppConnection(binder.connection)
            }

            override fun onServiceDisconnected(name: ComponentName) {
                AppHelper.setXmppConnection(null)
            }
        }
        AppHelper.setServiceConnection(connection)
        applicationContext.bindService(
            Intent(applicationContext, XMPPConnectionService::class.java),
            connection,
            Context.BIND_AUTO_CREATE
        )
    }

    override fun showToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    override fun showLoader() {
    }

    override fun hideLoader() {

    }
}
