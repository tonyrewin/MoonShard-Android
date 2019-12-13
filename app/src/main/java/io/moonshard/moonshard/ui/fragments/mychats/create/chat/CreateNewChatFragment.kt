package io.moonshard.moonshard.ui.fragments.mychats.create.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.moonshard.moonshard.R
import io.moonshard.moonshard.presentation.presenter.create_group.CreateNewChatPresenter
import io.moonshard.moonshard.presentation.view.create.CreateNewChatView
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

}
