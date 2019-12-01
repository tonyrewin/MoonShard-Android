package io.moonshard.moonshard.ui.fragments.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.moonshard.moonshard.R
import io.moonshard.moonshard.presentation.view.chat.ManageChatView
import moxy.MvpAppCompatFragment


class ManageChatFragment : MvpAppCompatFragment(),ManageChatView {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_manage_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }


}
