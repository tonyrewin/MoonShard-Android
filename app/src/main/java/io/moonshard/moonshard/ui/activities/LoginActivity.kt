package io.moonshard.moonshard.ui.activities


import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import de.adorsys.android.securestoragelibrary.SecurePreferences
import io.moonshard.moonshard.presentation.presenter.LoginPresenter
import io.moonshard.moonshard.presentation.view.LoginView
import io.moonshard.moonshard.services.XMPPConnectionService
import kotlinx.android.synthetic.main.activity_login.*
import moxy.presenter.InjectPresenter


class LoginActivity : BaseActivity(), LoginView {

    @InjectPresenter
    lateinit var presenter: LoginPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(io.moonshard.moonshard.R.layout.activity_login)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

        loginBtn?.setOnClickListener {
            val actualUserName: String
            if (editEmail?.text.toString().contains("@")) {
                showError("Вы ввели недопустимый символ")
            } else {
                actualUserName = editEmail.text.toString() + "@moonshard.tech"
                showLoader()
                saveLoginCredentials(actualUserName, editPassword.text.toString())
                startService()
            }
        }

        dontHaveText?.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
    }

    private fun startService() {
        startService(Intent(applicationContext, XMPPConnectionService::class.java))
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

    override fun onError(e: Exception) {
        runOnUiThread {
            hideLoader()
            e.message?.let { showError(it) } ?: showError("Произошла ошибка")
        }
    }

}
