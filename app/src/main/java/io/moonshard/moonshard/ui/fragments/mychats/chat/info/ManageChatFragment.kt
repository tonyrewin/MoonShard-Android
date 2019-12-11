package io.moonshard.moonshard.ui.fragments.mychats.chat.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.moonshard.moonshard.R
import io.moonshard.moonshard.presentation.presenter.chat.info.ManageChatPresenter
import io.moonshard.moonshard.presentation.view.chat.ManageChatView
import kotlinx.android.synthetic.main.fragment_manage_chat.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter


class ManageChatFragment : MvpAppCompatFragment(), ManageChatView {


    @InjectPresenter
    lateinit var presenter: ManageChatPresenter
    var idChat = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_manage_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            idChat = it.getString("chatId")
        }

        membersLayout?.setOnClickListener {
            showMembersScreen()
        }

        adminsLayout?.setOnClickListener {
            showAdminsScreen()
        }

        readBtn?.setOnClickListener {
            presenter.setNewNameChat(nameEt.text.toString(), idChat)
        }

        backBtn?.setOnClickListener {
            fragmentManager?.popBackStack()
        }
    }

    private fun showAdminsScreen() {
        val bundle = Bundle()
        bundle.putString("chatId", idChat)
        val fragment = AdminsFragment()
        fragment.arguments = bundle
        val ft = activity?.supportFragmentManager?.beginTransaction()
        ft?.add(R.id.container, fragment, "AdminsFragment")?.hide(this)
            ?.addToBackStack("AdminsFragment")
            ?.commit()
    }

    private fun showMembersScreen() {
        val bundle = Bundle()
        bundle.putString("chatId", idChat)
        val fragment = MembersChatFragment()
        fragment.arguments = bundle
        val ft = activity?.supportFragmentManager?.beginTransaction()
        ft?.add(R.id.container, fragment, "MembersChatFragment")?.hide(this)
            ?.addToBackStack("MembersChatFragment")
            ?.commit()
    }
}
