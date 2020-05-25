package io.moonshard.moonshard.ui.fragments.mychats

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import io.moonshard.moonshard.R
import io.moonshard.moonshard.ui.fragments.mychats.chat.EventsFragment
import io.moonshard.moonshard.ui.fragments.mychats.chat.MessagesFragment
import io.moonshard.moonshard.ui.fragments.mychats.create.AddChatFragment
import io.moonshard.moonshard.ui.fragments.mychats.create.AddNewEventFragment
import kotlinx.android.synthetic.main.item_test.view.*

class ViewPagerAdapter(private val tabItems: Array<TabItem>) : RecyclerView.Adapter<PagerVH>() {

    private val colors = intArrayOf(
        android.R.color.black,
        android.R.color.holo_red_light,
        android.R.color.holo_blue_dark,
        android.R.color.holo_purple
    )

    enum class TabItem(val fragmentClass: Class<out Fragment>, val titleResId: Int) {
        CHATS(ChatsFragment::class.java, R.string.chats),
        RECOMMENDATIONS(RecommendationsFragment::class.java, R.string.recommendations),
        CHAT(MessagesFragment::class.java, R.string.chat),
        EVENTS(EventsFragment::class.java, R.string.events),
        CREATE_EVENT(AddNewEventFragment::class.java, R.string.create_event),
        CREATE_CHAT(AddChatFragment::class.java,R.string.create_chat)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerVH =
        PagerVH(LayoutInflater.from(parent.context).inflate(R.layout.item_test, parent, false))

    override fun getItemCount(): Int = tabItems.size

    override fun onBindViewHolder(holder: PagerVH, position: Int) = holder.itemView.run {
        containers.setBackgroundResource(colors[position])
    }
}

class PagerVH(itemView: View) : RecyclerView.ViewHolder(itemView)