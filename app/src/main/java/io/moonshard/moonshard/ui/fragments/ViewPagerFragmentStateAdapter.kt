package io.moonshard.moonshard.ui.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import io.moonshard.moonshard.R
import io.moonshard.moonshard.ui.fragments.mychats.ChatsFragment
import io.moonshard.moonshard.ui.fragments.mychats.RecommendationsFragment
import io.moonshard.moonshard.ui.fragments.mychats.chat.EventsFragment
import io.moonshard.moonshard.ui.fragments.mychats.chat.MessagesFragment
import io.moonshard.moonshard.ui.fragments.mychats.create.AddChatFragment
import io.moonshard.moonshard.ui.fragments.mychats.create.AddNewEventFragment

class ViewPagerFragmentStateAdapter(
    private val tabItems: Array<TabItem>, val fragmentManager: FragmentManager, private val lifecycle: Lifecycle) : FragmentStateAdapter(fragmentManager,lifecycle) {

    var kek = arrayListOf<Fragment>()

    init {
        kek.add(MessagesFragment())
        kek.add(EventsFragment())
    }


    enum class TabItem(val fragmentClass: Class<out Fragment>, val titleResId: Int) {
        CHATS(ChatsFragment::class.java, R.string.chats),
        RECOMMENDATIONS(RecommendationsFragment::class.java, R.string.recommendations),
        CHAT(MessagesFragment::class.java, R.string.chat),
        EVENTS(EventsFragment::class.java, R.string.events),
        CREATE_EVENT(AddNewEventFragment::class.java, R.string.create_event),
        CREATE_CHAT(AddChatFragment::class.java, R.string.create_chat)
    }

    override fun createFragment(position: Int): Fragment {
        return kek[position]
    }

    private fun newInstance(fragmentClass: Class<out Fragment>): Fragment {
        try {
            return fragmentClass.newInstance()
        } catch (e: InstantiationException) {
            throw RuntimeException(
                "fragment must have public no-arg constructor: " + fragmentClass.name,
                e
            )
        }
    }

    override fun getItemCount(): Int = kek.size
}