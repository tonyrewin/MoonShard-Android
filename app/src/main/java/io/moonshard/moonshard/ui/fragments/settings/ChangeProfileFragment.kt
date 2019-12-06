package io.moonshard.moonshard.ui.fragments.settings

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.moonshard.moonshard.presentation.presenter.settings.ChangeProfilePresenter
import io.moonshard.moonshard.presentation.view.settings.ChangeProfileView
import kotlinx.android.synthetic.main.fragment_change_profile.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import org.jivesoftware.smackx.vcardtemp.packet.VCard


class ChangeProfileFragment : MvpAppCompatFragment(),ChangeProfileView {

    @InjectPresenter
    lateinit var presenter: ChangeProfilePresenter

    override fun setData(nickName: String?, description: String?) {
        nameTv?.setText(nickName?:"")
        descriptionTv?.setText(description ?: "")
    }

    override fun setAvatar(avatar: Bitmap?) {
        avatarIv?.setImageBitmap(avatar)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(
            io.moonshard.moonshard.R.layout.fragment_change_profile,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.getInfoProfile()
        presenter.getAvatar()

        readyBtn?.setOnClickListener {
            presenter.setData(nameTv?.text.toString(),descriptionTv?.text.toString())
        }

        backBtn?.setOnClickListener {
            fragmentManager?.popBackStack()
        }
    }

    override fun showProfile(){
        fragmentManager?.popBackStack()
        fragmentManager?.popBackStack()
    }
}
