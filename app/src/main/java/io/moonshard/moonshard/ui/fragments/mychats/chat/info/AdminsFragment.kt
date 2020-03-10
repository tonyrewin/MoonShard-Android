package io.moonshard.moonshard.ui.fragments.mychats.chat.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import io.moonshard.moonshard.R
import io.moonshard.moonshard.common.utils.setSafeOnClickListener
import io.moonshard.moonshard.presentation.presenter.chat.info.AdminsPresenter
import io.moonshard.moonshard.presentation.view.chat.info.AdminsView
import io.moonshard.moonshard.ui.adapters.chat.AdminListener
import io.moonshard.moonshard.ui.adapters.chat.AdminsAdapter
import io.moonshard.moonshard.ui.fragments.mychats.chat.MainChatFragment
import kotlinx.android.synthetic.main.fragment_admins.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import org.jivesoftware.smackx.muc.Occupant


class AdminsFragment : MvpAppCompatFragment(),
    AdminsView {

    @InjectPresenter
    lateinit var presenter: AdminsPresenter
    var idChat = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_admins, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()

        arguments?.let {
            idChat = it.getString("chatId")
            presenter.getAdmins(idChat)
        }

        backBtn?.setSafeOnClickListener {
            fragmentManager?.popBackStack()
        }

        addAdminLayout?.setSafeOnClickListener {
            showAddAdminScreen(idChat)
        }
    }

    private fun showAddAdminScreen(idChat: String) {
        (parentFragment as? MainChatFragment)?.showAddAdminScreen(idChat)

        /*
        val bundle = Bundle()
        bundle.putString("chatId", idChat)
        val fragment =
            AddAdminFragment()
        fragment.arguments = bundle
        val ft = activity?.supportFragmentManager?.beginTransaction()

        ft?.replace(R.id.mainContainer, fragment, "AddAdminFragment")?.hide(this)
            ?.addToBackStack("AddAdminFragment")
            ?.commit()

         */
    }

    override fun showToast(text: String) {
        Toast.makeText(activity, text, Toast.LENGTH_SHORT).show()
    }

    override fun showAdmins(admins: List<Occupant>) {
        (adminsRv?.adapter as? AdminsAdapter)?.setAdmins(admins)
    }

    private fun initAdapter() {
        adminsRv?.layoutManager = LinearLayoutManager(context)
        adminsRv?.adapter = AdminsAdapter(this, object : AdminListener {
            override fun remove(categoryName: String) {

            }
        }, arrayListOf())
    }

    override fun showProgressBar() {
        progressBar?.visibility = View.VISIBLE
    }

    override fun hideProgressBar() {
        progressBar?.visibility = View.GONE
    }
}
