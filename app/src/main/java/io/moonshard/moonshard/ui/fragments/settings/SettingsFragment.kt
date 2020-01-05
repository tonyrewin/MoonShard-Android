package io.moonshard.moonshard.ui.fragments.settings

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.R
import io.moonshard.moonshard.presentation.presenter.SettingsPresenter
import io.moonshard.moonshard.presentation.view.SettingsView
import io.moonshard.moonshard.ui.activities.auth.LoginActivity
import io.moonshard.moonshard.ui.activities.auth.RegisterActivity
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

        presenter.getAvatar()
        presenter.getName()
        logOut?.setOnClickListener {
            MainApplication.resetLoginCredentials()
            MainApplication.getXmppConnection().setStatus(false, "OFFLINE")
            presenter.logOut()
        }

        profileSettingsLayout?.setOnClickListener {
            showChangeProfileScreen()
        }

        securityLayout?.setOnClickListener {
            showSecurityScreen()
        }
    }

    override fun setData(nickName: String?, jidPart: String?) {
        nameTv?.text = nickName ?: "Имя"
        if(!jidPart.isNullOrBlank()){
            phoneTv?.text = "@$jidPart"
        }else{
            phoneTv?.text = "jid"
        }
    }

    override fun showRegistrationScreen() {
        val intentRegistration = Intent(activity, LoginActivity::class.java)
        intentRegistration.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_TASK_ON_HOME)
        startActivity(intentRegistration)
    }

    private fun showProfileScreen(){
            val fragment = ProfileFragment()
            val ft = activity?.supportFragmentManager?.beginTransaction()
            ft?.replace(R.id.container, fragment, "ProfileFragment")?.addToBackStack("ProfileFragment")
                ?.commit()
    }

    private fun showChangeProfileScreen() {
        val fragment = ChangeProfileFragment()
        val ft = activity?.supportFragmentManager?.beginTransaction()
        ft?.replace(R.id.container, fragment, "ChangeProfileFragment")
            ?.addToBackStack("ChangeProfileFragment")
            ?.commit()
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

    override fun setAvatar(avatar: Bitmap?) {
        avatarIv?.setImageBitmap(avatar)
    }

}
