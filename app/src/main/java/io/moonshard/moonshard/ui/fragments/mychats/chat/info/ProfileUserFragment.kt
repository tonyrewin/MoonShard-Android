package io.moonshard.moonshard.ui.fragments.mychats.chat.info

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import io.moonshard.moonshard.R
import io.moonshard.moonshard.presentation.presenter.chat.info.ProfileUserPresenter
import io.moonshard.moonshard.presentation.view.chat.info.ProfileUserView
import io.moonshard.moonshard.ui.fragments.mychats.chat.ChatFragment
import kotlinx.android.synthetic.main.fragment_profile_user.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter


class ProfileUserFragment : MvpAppCompatFragment(), ProfileUserView {

    @InjectPresenter
    lateinit var presenter: ProfileUserPresenter

    private var userJid = ""

    private var fromChatFragment = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            userJid = it.getString("userJid")
            fromChatFragment = it.getBoolean("fromChatFragment",false)
            presenter.getInfoProfile(userJid)
            presenter.getAvatar(userJid)
        }

        backBtn?.setOnClickListener {
            fragmentManager?.popBackStack()
        }

        sendMsgBtn?.setOnClickListener {
            if(fromChatFragment){
                fragmentManager?.popBackStack()
            }else{
                presenter.startChatWithUser(userJid)
            }
        }
    }

    override fun setData(nickName: String?, description: String?) {
        if (nickName != null) {
            nickNameTv?.text = nickName.toString()
        } else {
            nickNameTv?.text = "Имя"
        }

        if (description != null) {
            descriptionTv?.text = description.toString()
        } else {
            descriptionTv?.text = "Информация о пользователе не заполнена"
        }
    }

    override fun setAvatar(avatar: Bitmap?) {
        avatarIv?.setImageBitmap(avatar)
    }

    override fun showError(error: String) {
        Toast.makeText(activity, error, Toast.LENGTH_SHORT).show()
    }

    override fun showChatScreen(chatId: String) {
        val bundle = Bundle()
        bundle.putString("chatId", chatId)
        val chatFragment = ChatFragment()
        chatFragment.arguments = bundle
        val ft = activity?.supportFragmentManager?.beginTransaction()
        ft?.add(R.id.container, chatFragment, "chatScreen")?.hide(this)
            ?.addToBackStack("chatScreen")
            ?.commit()
    }


}
