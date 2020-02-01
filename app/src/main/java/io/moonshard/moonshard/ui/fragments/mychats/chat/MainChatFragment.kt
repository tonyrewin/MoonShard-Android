package io.moonshard.moonshard.ui.fragments.mychats.chat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import io.moonshard.moonshard.R
import io.moonshard.moonshard.ui.activities.MainActivity


class MainChatFragment : Fragment() {

    var idChat: String = ""
    var fromMap: Boolean = false
    var fromCreateNewChat:Boolean=false
    var stateChat:String = "join"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            idChat = it.getString("chatId")
            fromMap = it.getBoolean("fromMap",false)
            stateChat = it.getString("stateChat","join")
            fromCreateNewChat = it.getBoolean("fromCreateNewChat",false)
        }

        showChat()
    }

    private fun showChat() {
        val bundle = Bundle()
        bundle.putString("chatId", idChat)
        bundle.putBoolean("fromMap", fromMap)
        bundle.putString("stateChat", stateChat)
        val chatFragment = ChatFragment()
        chatFragment.arguments = bundle
        val ft = activity?.supportFragmentManager?.beginTransaction()
        ft?.replace(R.id.mainContainer, chatFragment)?.commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        fromMap.let {
            if (it) {
                (activity as? MainActivity)?.showBottomNavigationBar()
            }
        }
    }
}
