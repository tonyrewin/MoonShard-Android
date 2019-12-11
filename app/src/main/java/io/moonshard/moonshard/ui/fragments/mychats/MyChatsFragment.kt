package io.moonshard.moonshard.ui.fragments.mychats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.moonshard.moonshard.R
import io.moonshard.moonshard.ui.activities.MainActivity
import io.moonshard.moonshard.ui.adapters.chats.MyChatsPagerAdapter
import io.moonshard.moonshard.ui.fragments.mychats.create_group.AddChatFragment
import kotlinx.android.synthetic.main.fragment_my_chats.*


class MyChatsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_chats, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewPager()

        find?.setOnClickListener {
        }

        newChat?.setOnClickListener {
            (activity as MainActivity).hideBottomNavigationBar()
            val newFragment = AddChatFragment()
            val ft = activity?.supportFragmentManager?.beginTransaction()
            ft?.replace(R.id.container, newFragment,"AddChatFragment")?.addToBackStack("AddChatFragment")
                ?.commit()
        }
    }

    private fun initViewPager(){
        tabLayout.setupWithViewPager(viewPager)
        val sectionsPagerAdapter = MyChatsPagerAdapter(
            childFragmentManager,
            context!!,
            arrayOf(MyChatsPagerAdapter.TabItem.CHATS,MyChatsPagerAdapter.TabItem.RECOMMENDATIONS)
        )
        viewPager?.adapter = sectionsPagerAdapter
    }
}
