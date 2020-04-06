package io.moonshard.moonshard.ui.fragments.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import de.adorsys.android.securestoragelibrary.SecurePreferences
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.R
import io.moonshard.moonshard.common.utils.setSafeOnClickListener
import io.moonshard.moonshard.presentation.presenter.SettingsPresenter
import io.moonshard.moonshard.presentation.view.SettingsView
import io.moonshard.moonshard.ui.activities.MainActivity
import io.moonshard.moonshard.ui.activities.auth.LoginActivity
import io.moonshard.moonshard.ui.fragments.profile.ProfileFragment
import kotlinx.android.synthetic.main.fragment_settings_new.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter


class SettingsFragment : MvpAppCompatFragment(), SettingsView {

    @InjectPresenter
    lateinit var presenter: SettingsPresenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings_new, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).showBottomNavigationBar()

        disableInviteSwitch?.isChecked = !SecurePreferences.getBooleanValue("inviteInChats", true)
        switchNotifications?.isChecked =  SecurePreferences.getBooleanValue("notification_state", true)

        logOut?.setSafeOnClickListener {
            MainApplication.resetLoginCredentials()
            MainApplication.getXmppConnection().setStatus(false, "OFFLINE")
            presenter.logOut()
        }

        securityLayout?.setSafeOnClickListener {
            showSecurityScreen()
        }

        disableInviteSwitch?.setOnCheckedChangeListener { swichView, isChecked ->
            if(!MainApplication.getXmppConnection().isConnectionReady){
                swichView.isChecked = !isChecked
                disableInviteSwitch.isClickable = false
            }else{
                disableInviteSwitch.isClickable = false
                if(isChecked) presenter.disableInviteInChats() else presenter.enableInviteInChats()
            }
        }

        switchNotifications?.setOnCheckedChangeListener { swichView, isChecked ->
            if(isChecked){
                SecurePreferences.setValue("notification_state", true)
            }else{
                SecurePreferences.setValue("notification_state", false)
            }
        }
    }

    override fun showRegistrationScreen() {
        val intentRegistration = Intent(activity, LoginActivity::class.java)
        intentRegistration.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_TASK_ON_HOME)
        startActivity(intentRegistration)
    }

    private fun showSecurityScreen(){
        val fragment = SecurityFragment()
        val ft = activity?.supportFragmentManager?.beginTransaction()
        ft?.replace(R.id.container, fragment, "SecurityFragment")?.addToBackStack("SecurityFragment")
            ?.commit()
    }

    override fun showError(error: String) {
        Toast.makeText(activity, error, Toast.LENGTH_SHORT).show()
    }
}
