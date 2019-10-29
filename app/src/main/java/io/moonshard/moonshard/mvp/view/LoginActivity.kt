package io.moonshard.moonshard.mvp.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.arellomobile.mvp.MvpActivity
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import io.moonshard.moonshard.R
import io.moonshard.moonshard.mvp.presenter.LoginPresenter
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : MvpActivity(),LoginView {

    @InjectPresenter
    lateinit  var presenter: LoginPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginBtn.setOnClickListener {
            //presenter.showLoader()
        }

        dontHaveText.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
    }

    override fun showLoader() {
        progressBarLogin.visibility  = View.VISIBLE
    }

    override fun hideLoader() {
        progressBarLogin.visibility  = View.GONE
    }
}
