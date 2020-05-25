package io.moonshard.moonshard.ui.activities.auth

import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Toast
import io.moonshard.moonshard.R
import io.moonshard.moonshard.common.utils.setSafeOnClickListener
import io.moonshard.moonshard.presentation.presenter.auth.PasswordRecoveryPresenter
import io.moonshard.moonshard.presentation.view.auth.PasswordRecoveryView
import kotlinx.android.synthetic.main.activity_password_recovery.*
import moxy.MvpAppCompatActivity
import moxy.presenter.InjectPresenter

class PasswordRecoveryActivity : MvpAppCompatActivity(), PasswordRecoveryView {

    @InjectPresenter
    lateinit var presenter: PasswordRecoveryPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_recovery)

        recoveryPassBtn?.setSafeOnClickListener{
            presenter.recoveryPassword(editEmail.text.toString(),editPassword.text.toString())
        }

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

    override fun showProgressBar() {
        progressBarRecoveryPass?.visibility = View.VISIBLE
    }

    override fun hideProgressBar() {
        progressBarRecoveryPass?.visibility = View.GONE
    }

    override fun showError(error:String) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
    }

    override fun back(){
        onBackPressed()
    }
}
