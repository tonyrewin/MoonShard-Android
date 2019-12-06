package io.moonshard.moonshard.ui.fragments.settings

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import de.adorsys.android.securestoragelibrary.SecurePreferences
import io.moonshard.moonshard.R
import io.moonshard.moonshard.presentation.presenter.SettingsPresenter
import io.moonshard.moonshard.presentation.view.SettingsView
import io.moonshard.moonshard.ui.activities.RegisterActivity
import io.moonshard.moonshard.ui.fragments.create_group.CreateNewChatFragment
import kotlinx.android.synthetic.main.fragment_profile.*
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
        return inflater.inflate(R.layout.fragment_settings_new, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter.getAvatar()
        presenter.getName()
        logOut?.setOnClickListener {
            presenter.logOut()
            clearLoginCredentials()
        }

        profileSettingsLayout?.setOnClickListener {
            showProfileScreen()
        }
    }

    override fun setData(nickName: String?, jidPart: String?) {
        nameTv?.text = nickName ?: "Имя"
        phoneTv?.text = jidPart ?: "jid"
    }

    override fun showRegistrationScreen() {
        val intentRegistration = Intent(activity, RegisterActivity::class.java)
        intentRegistration.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_TASK_ON_HOME)
        startActivity(intentRegistration)
    }

    fun showProfileScreen(){
            val fragment = ProfileFragment()
            val ft = activity?.supportFragmentManager?.beginTransaction()
            ft?.replace(R.id.container, fragment, "ProfileFragment")?.addToBackStack("ProfileFragment")
                ?.commit()
    }

    override fun showError(error: String) {
        Toast.makeText(activity, error, Toast.LENGTH_SHORT).show()
    }

    private fun clearLoginCredentials() {
        SecurePreferences.setValue("jid", "")
        SecurePreferences.setValue("pass", "")
        SecurePreferences.setValue("logged_in", false)
    }

    override fun setAvatar(avatar: Bitmap?) {
        avatarIv?.setImageBitmap(avatar)
    }

}
