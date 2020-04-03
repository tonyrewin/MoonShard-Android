package io.moonshard.moonshard.ui.fragments.profile

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.R
import io.moonshard.moonshard.common.utils.setSafeOnClickListener
import io.moonshard.moonshard.presentation.presenter.settings.ProfilePresenter
import io.moonshard.moonshard.presentation.view.settings.ProfileView
import io.moonshard.moonshard.ui.fragments.settings.ChangeProfileFragment
import kotlinx.android.synthetic.main.fragment_profile.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter


class ProfileFragment : MvpAppCompatFragment(), ProfileView {

    @InjectPresenter
    lateinit var presenter: ProfilePresenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.getInfoProfile()
        presenter.getAvatar()

        changeBtn?.setSafeOnClickListener {
            showChangeProfileScreen()
        }

        backBtn?.setSafeOnClickListener {
            fragmentManager?.popBackStack()
        }

        profileSettingsLayout?.setSafeOnClickListener {
            showChangeProfileScreen()
        }
    }

    private fun showChangeProfileScreen() {
        val fragment =
            ChangeProfileFragment()
        val ft = activity?.supportFragmentManager?.beginTransaction()
        ft?.replace(R.id.container, fragment, "ChangeProfileFragment")
            ?.addToBackStack("ChangeProfileFragment")
            ?.commit()
    }

    override fun setData(nickName: String?, description: String?,jidPart:String?) {
        MainApplication.getMainUIThread().post {
            nameTv?.text = nickName ?: "Имя"
            if(!jidPart.isNullOrBlank()){
                myJid?.text = "@$jidPart"
            }else{
                myJid?.text = "jid"
            }

            if (description != null) {
                descriptionTv?.text = description.toString()
            } else {
                descriptionTv?.text = "Информация о вас не заполнена"
            }
        }
    }

    override fun setAvatar(avatar: Bitmap?) {
        MainApplication.getMainUIThread().post {
            avatarIv?.setImageBitmap(avatar)
        }
    }

    override fun showError(error: String) {
        Toast.makeText(activity, error, Toast.LENGTH_SHORT).show()
    }
}
