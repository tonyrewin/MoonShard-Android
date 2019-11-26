package io.moonshard.moonshard.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import de.adorsys.android.securestoragelibrary.SecurePreferences
import io.moonshard.moonshard.presentation.presenter.SettingsPresenter
import io.moonshard.moonshard.presentation.view.SettingsView
import io.moonshard.moonshard.ui.activities.RegisterActivity
import kotlinx.android.synthetic.main.fragment_settings.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter


class SettingsFragment : MvpAppCompatFragment(), SettingsView {

    @InjectPresenter
    lateinit var presenter: SettingsPresenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(io.moonshard.moonshard.R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        logOut?.setOnClickListener {
            presenter.logOut()
            clearLoginCredentials()
        }
    }

    override fun showRegistrationScreen() {
        val intentRegistration = Intent(activity, RegisterActivity::class.java)
        intentRegistration.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_TASK_ON_HOME)
        startActivity(intentRegistration)
    }

    override fun showError(error: String) {
        Toast.makeText(activity, error, Toast.LENGTH_SHORT).show()
    }

    private fun clearLoginCredentials() {
        SecurePreferences.setValue("jid", "")
        SecurePreferences.setValue("pass", "")
        SecurePreferences.setValue("logged_in", false)
    }

}
