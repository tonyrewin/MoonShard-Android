package io.moonshard.moonshard.ui.fragments.settings

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.moonshard.moonshard.R
import io.moonshard.moonshard.presentation.presenter.settings.ProfilePresenter
import io.moonshard.moonshard.presentation.view.settings.ProfileView
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
    }

    override fun setData(nickName: String?, description: String?) {
        if(nickName!=null){
            nickNameTv?.text = nickName.toString()
        }else{
            nickNameTv?.text = "Имя"
        }

        if(description!=null){
            descriptionTv?.text = description.toString()
        }else{
            descriptionTv?.text = "Информация о вас не заполнена"
        }
    }

    override fun setAvatar(avatar: Bitmap?) {
        avatarIv?.setImageBitmap(avatar)
    }
}
