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
import org.jivesoftware.smack.SmackException
import org.jivesoftware.smack.XMPPException


class RegisterActivity : BaseActivity(), RegisterView {

    @InjectPresenter
    lateinit var presenter: RegisterPresenter

    var isRegistrationSuccess = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (checkFirstStart()) {
            setTheme(R.style.AppTheme)
            startIntro()
        } else {
            isAuth()
        }
    }

    private fun checkFirstStart(): Boolean {
        return SecurePreferences.getBooleanValue("first_start", true)
    }

    fun startIntro() {
        val intent = Intent(this, MainIntroActivity::class.java)
        startActivity(intent)
    }

    private fun saveLoginCredentials(email: String, password: String) {
        SecurePreferences.setValue("jid", email)
        SecurePreferences.setValue("pass", password)
    }

    override fun startService() {
        startService(Intent(applicationContext, XMPPConnectionService::class.java))
    }

    override fun showError(error: String) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
    }

    override fun onSuccess() {

    }

    override fun successRegistration() {
        isRegistrationSuccess = true
    }

    override fun onError(e: Exception) {
        runOnUiThread {
            hideLoader()
            setTheme(R.style.AppTheme)

            when (e) {
                is XMPPException -> {
                    showError("Произошла ошибка на сервере")
                }
                is SmackException.NoResponseException -> {
                    showError("Время ожидания ответа от сервера истекло")
                }
                is SmackException.NotConnectedException -> {
                    showError("Отсутствует интернет-соединение")
                }
                else -> {
                    e.message?.let { showError(it) } ?: showError("Произошла ошибка")
                }
            }
        }
    }

    override fun onAuthenticated() {
        setTheme(R.style.AppTheme)
        runOnUiThread {
            hideLoader()
            if (isRegistrationSuccess) {
                showStartProfileScreen()
            } else {
                showContactsScreen()
            }
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

    override fun showToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    override fun showLoader() {
        progressBarReg?.visibility = View.VISIBLE
    }

    override fun hideLoader() {
        progressBarReg?.visibility = View.GONE
    }

    fun isAuth(){
        val logged = SecurePreferences.getBooleanValue("logged_in", false)
        if (!logged) {
           setRegisterLayout()
        }else{
            presenter.login()
        }
    }

    override fun setRegisterLayout(){
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

        setTheme(R.style.AppTheme)
        setContentView(R.layout.activity_register)

        alreadyHaveText?.setSafeOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        registerBtn?.setSafeOnClickListener {
            if (nickNameEt?.text.toString().contains("@")) {
                showError("Вы ввели недопустимый символ")
            } else {
                val actualUserName = nickNameEt.text.toString() + "@moonshard.tech"
                showLoader()
                presenter.registerOnServer(actualUserName, editPassword.text.toString())
            }
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
