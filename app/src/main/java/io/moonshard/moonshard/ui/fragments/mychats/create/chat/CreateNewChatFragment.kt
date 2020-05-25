package io.moonshard.moonshard.ui.fragments.mychats.create.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import io.moonshard.moonshard.R
import io.moonshard.moonshard.common.utils.setSafeOnClickListener
import io.moonshard.moonshard.presentation.presenter.create_group.CreateNewChatPresenter
import io.moonshard.moonshard.presentation.view.create.CreateNewChatView
import io.moonshard.moonshard.ui.activities.MainActivity
import kotlinx.android.synthetic.main.fragment_create_new_chat.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter

class CreateNewChatFragment : MvpAppCompatFragment(), CreateNewChatView {

    @InjectPresenter
    lateinit var presenter: CreateNewChatPresenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_new_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        newChat?.setSafeOnClickListener {
            presenter.createGroupChat(nameTv.text.toString(),descriptionTv.text.toString())
        }

        back?.setSafeOnClickListener {
            fragmentManager?.popBackStack()
        }
    }

    override fun showChatScreen(chatId: String) {
        (activity as? MainActivity)?.showMainChatScreen(chatId,fromCreateNewChat = true)
    }

    override fun showToast(text: String) {
        Toast.makeText(context!!, text, Toast.LENGTH_SHORT).show()
    }

    override fun showProgressBar() {
        progressBar?.visibility = View.VISIBLE
    }

    override fun hideProgressBar() {
        progressBar?.visibility = View.GONE
    }
}
