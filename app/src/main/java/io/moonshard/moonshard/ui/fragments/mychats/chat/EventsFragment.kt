package io.moonshard.moonshard.ui.fragments.mychats.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import io.moonshard.moonshard.R
import io.moonshard.moonshard.models.api.RoomPin
import io.moonshard.moonshard.presentation.presenter.EventsPresenter
import io.moonshard.moonshard.presentation.view.chat.EventsView
import io.moonshard.moonshard.ui.adapters.chat.EventAdapter
import io.moonshard.moonshard.ui.adapters.chat.EventListener
import kotlinx.android.synthetic.main.fragment_events.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter


class EventsFragment : MvpAppCompatFragment(), EventsView {

    @InjectPresenter
    lateinit var presenter: EventsPresenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_events, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()
        presenter.getRooms()
    }

    private fun initAdapter() {
        eventsRv?.layoutManager = LinearLayoutManager(context)
        eventsRv?.adapter = EventAdapter(object : EventListener {
            override fun eventClick(categoryName: String) {

            }
        }, arrayListOf())
    }

    override fun setEvents(events: ArrayList<RoomPin>) {
        (eventsRv?.adapter as? EventAdapter)?.update(events)
    }

    override fun showError(error: String) {
        Toast.makeText(activity, error, Toast.LENGTH_SHORT).show()
    }
}
