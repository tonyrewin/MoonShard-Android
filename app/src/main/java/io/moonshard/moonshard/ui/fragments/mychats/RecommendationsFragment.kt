package io.moonshard.moonshard.ui.fragments.mychats

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager

import io.moonshard.moonshard.R
import io.moonshard.moonshard.models.dbEntities.ChatEntity
import io.moonshard.moonshard.presentation.presenter.chat.RecommendationsPresenter
import io.moonshard.moonshard.presentation.view.chat.RecommendationsView
import io.moonshard.moonshard.ui.activities.MainActivity
import io.moonshard.moonshard.ui.adapters.chats.RecommendationsAdapter
import io.moonshard.moonshard.ui.adapters.chats.RecommendationsListener
import kotlinx.android.synthetic.main.fragment_recommendations.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter


class RecommendationsFragment : MvpAppCompatFragment(), RecommendationsView {

    @InjectPresenter
    lateinit var presenter: RecommendationsPresenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recommendations, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()

        presenter.getRecommendation()
    }

    private fun initAdapter(){
        recommendationsRv?.layoutManager = LinearLayoutManager(context)
        recommendationsRv?.adapter = RecommendationsAdapter(object : RecommendationsListener {
            override fun recommendationsClick(jid: String) {
                showChatScreen(jid)
            }
        }, arrayListOf())
    }

    override fun showChatScreen(chatId: String) {
        (activity as? MainActivity)?.showMainChatScreen(chatId = chatId)
    }

    override fun showRecommendations(recommendations: List<ChatEntity>) {
        (recommendationsRv?.adapter as? RecommendationsAdapter)?.updateRecommendations(recommendations)
    }
}
