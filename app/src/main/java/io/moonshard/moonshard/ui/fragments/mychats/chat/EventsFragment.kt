package io.moonshard.moonshard.ui.fragments.mychats.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import io.moonshard.moonshard.R
import io.moonshard.moonshard.common.utils.setSafeOnClickListener
import io.moonshard.moonshard.db.ChatRepository
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

    var idChat: String = ""


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
        ChatRepository.idChatCurrent?.let {
            idChat = it
            addNewEventBtn.setSafeOnClickListener {
                showCreateNewEventScreen(idChat)
            }
        }
    }

    private fun showCreateNewEventScreen(idChat: String) {
        (parentFragment as? ChatFragment)?.showCreateNewEventScreen(idChat)
    }

   override fun showChatScreen(jid:String){
       (parentFragment as? ChatFragment)?.showChatFragment(jid)
    }

    private fun initAdapter() {
        eventsRv?.layoutManager = LinearLayoutManager(context)
        eventsRv?.adapter = EventAdapter(object : EventListener {
            override fun eventClick(event: RoomPin) {
                event.roomId?.let { showChatScreen(it) }
            }
        }, arrayListOf())
    }

    override fun setEvents(events: ArrayList<RoomPin>) {
        (eventsRv?.adapter as? EventAdapter)?.update(events)
    }

    override fun showError(error: String) {
        Toast.makeText(activity, error, Toast.LENGTH_SHORT).show()
    }

    override fun isShowCreateEventLayout(isShow: Boolean, isAdmin: Boolean) {
        if (isShow) {
            if (isAdmin) {
                eventsRv.visibility = View.GONE
                createNewEventLayout.visibility = View.VISIBLE
                createTextView.visibility = View.VISIBLE
                addNewEventBtn?.visibility = View.VISIBLE
            } else {
                eventsRv.visibility = View.GONE
                createNewEventLayout.visibility = View.VISIBLE
                createTextView.visibility = View.GONE
                addNewEventBtn?.visibility = View.GONE
            }
        } else {
            eventsRv.visibility = View.VISIBLE
            createNewEventLayout.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}
