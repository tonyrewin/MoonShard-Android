package io.moonshard.moonshard.ui.fragments.mychats.create.event

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager

import io.moonshard.moonshard.R
import io.moonshard.moonshard.db.ChooseChatRepository
import io.moonshard.moonshard.ui.adapters.RvTimeListener
import io.moonshard.moonshard.ui.adapters.TimeGroupChatAdapter
import kotlinx.android.synthetic.main.fragment_time_group_chat.*


class TimeEventFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_time_group_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        back?.setOnClickListener {
            fragmentManager?.popBackStack()
        }

        val times = arrayListOf<String>()
        times.add("1 день")
        times.add("2 дня")
        times.add("3 дня")
        times.add("4 дня")
        times.add("5 дней")
        times.add("6 дней")
        times.add("Неделя")


        timesRv?.layoutManager = LinearLayoutManager(view.context)
        timesRv?.adapter = TimeGroupChatAdapter(object : RvTimeListener {
            override fun clickChat(time: String) {
                ChooseChatRepository.time = time
            }
        }, times)
    }
}
