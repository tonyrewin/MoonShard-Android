package io.moonshard.moonshard.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import io.moonshard.moonshard.R
import io.moonshard.moonshard.ui.adapters.MyChatsAdapter
import io.moonshard.moonshard.ui.adapters.RvListener
import kotlinx.android.synthetic.main.fragment_chats.*


class ChatsFragment : Fragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chatsRv?.let {
            chatsRv.layoutManager = LinearLayoutManager(view.context)



            chatsRv.adapter = MyChatsAdapter(object : RvListener{
                override fun clickChat(driver: String) {
                    Log.d("chatsFragment","was click on chat item")
                }
            }, arrayListOf())
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chats, container, false)
    }
}
