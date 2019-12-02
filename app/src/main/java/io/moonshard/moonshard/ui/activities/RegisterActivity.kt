package io.moonshard.moonshard.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import de.adorsys.android.securestoragelibrary.SecurePreferences
import io.moonshard.moonshard.LoginCredentials
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

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        startService()
        auth()
        alreadyHaveText?.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        registerBtn?.setOnClickListener {
            presenter.register(editEmail.text.toString(), editPassword.text.toString())
        }
    }

    override fun onSuccess() {
        saveLoginCredentials(editEmail.text.toString()+"@moonshard.tech", editPassword.text.toString())
        startService()
    }

    private fun saveLoginCredentials(email: String, password: String) {
        SecurePreferences.setValue("jid", email)
        SecurePreferences.setValue("pass", password)
    }

    private fun startService() {
        startService(Intent(applicationContext, XMPPConnectionService::class.java))
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
        runOnUiThread {
            hideLoader()
        }
        showContactsScreen()
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
