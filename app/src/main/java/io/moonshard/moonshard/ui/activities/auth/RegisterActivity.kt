package io.moonshard.moonshard.ui.activities.auth

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.method.PasswordTransformationMethod
import android.text.style.UnderlineSpan
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import de.adorsys.android.securestoragelibrary.SecurePreferences
import io.moonshard.moonshard.R
import io.moonshard.moonshard.common.utils.setSafeOnClickListener
import io.moonshard.moonshard.presentation.presenter.RegisterPresenter
import io.moonshard.moonshard.presentation.view.RegisterView
import io.moonshard.moonshard.services.XMPPConnectionService
import io.moonshard.moonshard.ui.activities.BaseActivity
import io.moonshard.moonshard.ui.activities.MainActivity
import io.moonshard.moonshard.ui.activities.onboard.MainIntroActivity
import io.moonshard.moonshard.ui.activities.onboardregistration.StartProfileActivity
import kotlinx.android.synthetic.main.activity_register.*
import moxy.presenter.InjectPresenter


class RegisterActivity : BaseActivity(), RegisterView {

    @InjectPresenter
    lateinit var presenter: RegisterPresenter

    var isRegistration = false

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)

        if (checkFirstStart()) {
            startIntro()
        } else {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

            startService() // if we want open this screen when logout we must use handle 5 sec
            auth()
            alreadyHaveText?.setSafeOnClickListener {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }

            registerBtn?.setSafeOnClickListener {
                presenter.register(editEmail.text.toString(), editPassword.text.toString())
            }

            val content = SpannableString("Уже есть аккаунт? Войти")
            content.setSpan(UnderlineSpan(), 18, content.length, 0)
            alreadyHaveText?.text = content

            var isSecurity = true
            visiblePassBtn?.setSafeOnClickListener {
                if (isSecurity) {
                    editPassword?.transformationMethod = null
                    visiblePassBtn?.setImageResource(R.drawable.ic_pass_on)
                    isSecurity = false
                } else {
                    editPassword?.transformationMethod = PasswordTransformationMethod()
                    visiblePassBtn?.setImageResource(R.drawable.ic_pass_off)
                    isSecurity = true
                }
            }
        }
    }

    private fun checkFirstStart(): Boolean {
        return SecurePreferences.getBooleanValue("first_start", true)
    }

    fun startIntro() {
        val intent = Intent(this, MainIntroActivity::class.java)
        startActivity(intent)
    }

    override fun onSuccess() {
        isRegistration = true
        saveLoginCredentials(
            editEmail.text.toString() + "@moonshard.tech",
            editPassword.text.toString()
        )
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

    override fun showStartProfileScreen() {
        val intentStartProfileActivity = Intent(this, StartProfileActivity::class.java)
        startActivity(intentStartProfileActivity)
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
            if (isRegistration) {
                showStartProfileScreen()
            } else {
                showContactsScreen()
            }
        }
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
