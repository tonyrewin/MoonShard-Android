package io.moonshard.moonshard.mvp.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.arellomobile.mvp.MvpActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import io.moonshard.moonshard.R
import io.moonshard.moonshard.mvp.presenter.LoginPresenter
import io.moonshard.moonshard.mvp.view.LoginView
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : MvpActivity(), LoginView {

    override fun test() {
        //val matrixInstance = Matrix.getInstance(applicationContext)
    }

    @InjectPresenter
    lateinit var presenter: LoginPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginBtn.setOnClickListener {
            presenter.login(
                "https://matrix.moonshard.tech", "https://vector.im",
                editEmail.text.toString(), editPassword.text.toString()
            )
        }

        dontHaveText.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
    }

    override fun showContactsScreen() {
        val intentContactsActivity= Intent(this,ContactsActivity::class.java)
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
