package io.moonshard.moonshard.ui.fragments.mychats.chat.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import io.moonshard.moonshard.R
import io.moonshard.moonshard.common.utils.setSafeOnClickListener
import io.moonshard.moonshard.presentation.presenter.chat.info.AddAdminPresenter
import io.moonshard.moonshard.presentation.view.chat.info.AddAdminView
import io.moonshard.moonshard.ui.fragments.mychats.chat.MainChatFragment
import kotlinx.android.synthetic.main.fragment_add_admin.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter


class AddAdminFragment : MvpAppCompatFragment(),
    AddAdminView {

    @InjectPresenter
    lateinit var presenter: AddAdminPresenter

    var idChat = ""
    var userJid = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_admin, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            idChat = it.getString("chatId")
        }

        addAdminBtn?.setSafeOnClickListener {
            presenter.addAdmin(nameTv.text.toString(),idChat)
        }
    }

    override fun showError(error: String) {
        Toast.makeText(context!!, error, Toast.LENGTH_SHORT).show()
    }

    override fun showChatScreen() {
        (parentFragment as? MainChatFragment)?.moveAndClearPopBackStackChild()

        /*
        val bundle = Bundle()
        bundle.putString("chatId", idChat)
        val mainChatFragment = MainChatFragment()
        mainChatFragment.arguments = bundle
        val ft = activity?.supportFragmentManager?.beginTransaction()
        ft?.replace(R.id.container, mainChatFragment)?.hide(this)?.addToBackStack(null)
            ?.commit()
         */
    }


}
