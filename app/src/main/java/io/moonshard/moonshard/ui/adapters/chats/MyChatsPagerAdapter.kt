package io.moonshard.moonshard.ui.adapters.chats

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import io.moonshard.moonshard.R
import io.moonshard.moonshard.ui.fragments.mychats.ChatsFragment
import io.moonshard.moonshard.ui.fragments.mychats.RecommendationsFragment
import io.moonshard.moonshard.ui.fragments.mychats.chat.EventsFragment
import io.moonshard.moonshard.ui.fragments.mychats.chat.MessagesFragment

class MyChatsPagerAdapter(
    fragmentManager: FragmentManager, val context: Context,
    private val tabItems: Array<TabItem>
) :
    FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    enum class TabItem(val fragmentClass: Class<out Fragment>, val titleResId: Int) {
        CHATS(ChatsFragment::class.java, R.string.chats),
        RECOMMENDATIONS(RecommendationsFragment::class.java, R.string.recommendations),
        CHAT(MessagesFragment::class.java, R.string.chat),
        EVENTS(EventsFragment::class.java, R.string.events);
    }

    override fun getItem(position: Int): Fragment {
        return newInstance(tabItems[position].fragmentClass)
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

    override fun getPageTitle(position: Int): CharSequence? {
        return context.getString(tabItems[position].titleResId)
    }

    override fun getCount(): Int {
        return tabItems.size
    }
}