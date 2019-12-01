package io.moonshard.moonshard.ui.fragments.chat

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager

import io.moonshard.moonshard.R
import io.moonshard.moonshard.presentation.presenter.chat.MembersChatPresenter
import io.moonshard.moonshard.presentation.view.chat.MembersChatView
import io.moonshard.moonshard.ui.adapters.chat.MemberListener
import io.moonshard.moonshard.ui.adapters.chat.MembersAdapter
import kotlinx.android.synthetic.main.fragment_members_chat.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import org.jivesoftware.smackx.muc.Occupant


class MembersChatFragment : MvpAppCompatFragment(),MembersChatView {

    @InjectPresenter
    lateinit var presenter: MembersChatPresenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_members_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()

        arguments?.let {
            val idChat = it.getString("chatId")
            presenter.getMembers(idChat!!)
        }

        backBtn?.setOnClickListener {
            fragmentManager?.popBackStack()
        }
    }

    override fun showError(error: String) {
        Toast.makeText(activity, error, Toast.LENGTH_SHORT).show()
    }

    override fun showMembers(members:List<Occupant>) {
        (membersRv?.adapter as MembersAdapter).setMembers(members)
    }

    private fun initAdapter(){
        membersRv?.layoutManager = LinearLayoutManager(context)
        membersRv?.adapter = MembersAdapter( object : MemberListener {
            override fun remove(categoryName: String) {

            }
        }, arrayListOf())
    }
}
