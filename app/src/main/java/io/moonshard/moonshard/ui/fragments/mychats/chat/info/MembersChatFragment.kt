package io.moonshard.moonshard.ui.fragments.mychats.chat.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager

import io.moonshard.moonshard.R
import io.moonshard.moonshard.common.utils.setSafeOnClickListener
import io.moonshard.moonshard.presentation.presenter.chat.info.MembersChatPresenter
import io.moonshard.moonshard.presentation.view.chat.MembersChatView
import io.moonshard.moonshard.ui.adapters.chat.MemberListener
import io.moonshard.moonshard.ui.adapters.chat.MembersAdapter
import kotlinx.android.synthetic.main.fragment_members_chat.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import org.jivesoftware.smackx.muc.Occupant
import org.jxmpp.jid.EntityFullJid
import org.jxmpp.jid.impl.JidCreate


class MembersChatFragment : MvpAppCompatFragment(), MembersChatView {

    @InjectPresenter
    lateinit var presenter: MembersChatPresenter

    var idChat = ""

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
            idChat = it.getString("chatId")
            presenter.getMembers(idChat)
        }

        backBtn?.setSafeOnClickListener {
            fragmentManager?.popBackStack()
        }

        addMemberBtn?.setSafeOnClickListener {
            showInvitewNewUserScreen(idChat)
        }
    }

    override fun showError(error: String) {
        Toast.makeText(activity!!, error, Toast.LENGTH_SHORT).show()
    }

    override fun showMembers(members: List<Occupant>) {
        (membersRv?.adapter as MembersAdapter).setMembers(members)
    }

    //todo maybe replace
    fun showProfileUser(jid: String) {
        val bundle = Bundle()
        bundle.putString("userJid", jid)
        val fragment = ProfileUserFragment()
        fragment.arguments = bundle
        val ft = activity?.supportFragmentManager?.beginTransaction()
        ft?.replace(R.id.mainContainer, fragment, "ProfileUserFragment")?.hide(this)
            ?.addToBackStack("ProfileUserFragment")
            ?.commit()
    }

    private fun showInvitewNewUserScreen(idChat: String) {
        val bundle = Bundle()
        bundle.putString("chatId", idChat)
        val fragment =
            InviteUserFragment()
        fragment.arguments = bundle
        val ft = activity?.supportFragmentManager?.beginTransaction()
        ft?.replace(R.id.mainContainer, fragment, "InviteUserFragment")?.hide(this)
            ?.addToBackStack("InviteUserFragment")
            ?.commit()
    }

    private fun initAdapter() {
        membersRv?.layoutManager = LinearLayoutManager(context)
        membersRv?.adapter = MembersAdapter(object : MemberListener {
            override fun clickMember(member: String) {
                showProfileUser(member)
            }

            override fun remove(member: Occupant) {
                presenter.kickUser(idChat,JidCreate.entityFullFrom(idChat.toString()+"/"+ member.nick),member)
            }
        }, arrayListOf(),true)
    }

    override fun removeMember(member:Occupant){
        (membersRv?.adapter as MembersAdapter).removeMember(member)
    }
}
