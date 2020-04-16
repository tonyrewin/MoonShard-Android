package io.moonshard.moonshard.ui.activities.auth


import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.method.PasswordTransformationMethod
import android.text.style.UnderlineSpan
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.google.zxing.integration.android.IntentIntegrator
import de.adorsys.android.securestoragelibrary.SecurePreferences
import io.moonshard.moonshard.R
import io.moonshard.moonshard.common.utils.setSafeOnClickListener
import io.moonshard.moonshard.presentation.presenter.LoginPresenter
import io.moonshard.moonshard.presentation.view.LoginView
import io.moonshard.moonshard.services.XMPPConnectionService
import io.moonshard.moonshard.ui.activities.BaseActivity
import io.moonshard.moonshard.ui.activities.MainActivity
import kotlinx.android.synthetic.main.activity_login.*
import moxy.presenter.InjectPresenter
import org.jivesoftware.smack.SmackException
import org.jivesoftware.smack.XMPPException


class LoginActivity : BaseActivity(), LoginView {

    @InjectPresenter
    lateinit var presenter: LoginPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        val registertext = getString(R.string.register)
        val contentNotHaveText = SpannableString("" + getString(R.string.dont_have_an_account_yet) + " " + registertext)
        contentNotHaveText.setSpan(UnderlineSpan(), (contentNotHaveText.length-registertext.length), contentNotHaveText.length, 0)
        dontHaveText.text = contentNotHaveText

        val contentForgotPass= SpannableString("" + getString(R.string.forgot_your_password) + "")
        contentForgotPass.setSpan(UnderlineSpan(), 0, contentForgotPass.length, 0)
        forgotPassTv.text = contentForgotPass

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

        loginBtn?.setSafeOnClickListener {
            val actualUserName: String
            if (editEmail?.text.toString().contains("@")) {
                showError("" + getString(R.string.you_entered_an_invalid_character) + "")
            } else {
                actualUserName = editEmail.text.toString() + "@moonshard.tech"
                showLoader()
                saveLoginCredentials(actualUserName, editPassword.text.toString())
                startService()
            }
        }

        dontHaveText?.setSafeOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }

        forgotPassTv?.setSafeOnClickListener {
            startActivity(Intent(this, PasswordRecoveryActivity::class.java))
        }
    }

    private fun startService() {
        startService(Intent(applicationContext, XMPPConnectionService::class.java))
    }

    override fun onAuthenticated() {
        presenter.addSupportChat()
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

            when (e) {
                is XMPPException -> {
                    showError("" + getString(R.string.server_error_occurred) + "")
                }
                is SmackException.NoResponseException -> {
                    showError("" + getString(R.string.server_response_timed_out) + "")
                }
                is SmackException.NotConnectedException -> {
                    showError("" + getString(R.string.no_internet_connection) + "")
                }
                else -> {
                    e.message?.let { showError(it) } ?: showError("" + getString(R.string.an_error_has_occurred) + "")
                }
            }
        }
    }

}
