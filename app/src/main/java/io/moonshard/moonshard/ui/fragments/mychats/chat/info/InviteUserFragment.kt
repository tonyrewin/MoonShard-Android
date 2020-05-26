package io.moonshard.moonshard.ui.fragments.mychats.chat.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import io.moonshard.moonshard.R
import io.moonshard.moonshard.common.utils.setSafeOnClickListener
import io.moonshard.moonshard.presentation.presenter.chat.info.InviteUserPresenter
import io.moonshard.moonshard.presentation.view.chat.info.InviteUserView
import io.moonshard.moonshard.ui.fragments.mychats.chat.MainChatFragment
import kotlinx.android.synthetic.main.fragment_invite_user.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter


class InviteUserFragment : MvpAppCompatFragment(), InviteUserView {

    @InjectPresenter
    lateinit var presenter: InviteUserPresenter
    var idChat = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_invite_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            idChat = it.getString("chatId")
        }

        inviteUserBtn?.setSafeOnClickListener {
            presenter.inviteUser(nameTv.text.toString(), idChat)
        }

        back?.setSafeOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    override fun showError(error: String) {
        Toast.makeText(context!!, error, Toast.LENGTH_SHORT).show()
    }

    override fun showChatScreen() {
        (parentFragment as? MainChatFragment)?.moveAndClearPopBackStackChild()
    }
}
