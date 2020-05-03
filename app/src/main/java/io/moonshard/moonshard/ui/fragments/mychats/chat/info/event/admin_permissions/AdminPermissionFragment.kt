package io.moonshard.moonshard.ui.fragments.mychats.chat.info.event.admin_permissions

import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson

import io.moonshard.moonshard.R
import io.moonshard.moonshard.common.utils.setSafeOnClickListener
import io.moonshard.moonshard.models.AdminPermission
import io.moonshard.moonshard.presentation.presenter.chat.info.AdminPermissionPresenter
import io.moonshard.moonshard.presentation.view.chat.info.AdminPermissionView
import io.moonshard.moonshard.ui.adapters.chat.AdminPermissionAdapter
import io.moonshard.moonshard.ui.adapters.chat.AdminPermissionListener
import kotlinx.android.synthetic.main.fragment_admin_permission.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import org.jivesoftware.smackx.muc.Occupant


class AdminPermissionFragment : MvpAppCompatFragment(),AdminPermissionView {

    @InjectPresenter
    lateinit var presenter: AdminPermissionPresenter

    var idChat: String = ""
    var occupant:Occupant? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_permission, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            idChat = it.getString("chatId")
            occupant = Gson().fromJson(it.getString("occupant"),Occupant::class.java)
        }

        //presenter.getAvatar("kek")
        initAdapter()

        backBtn?.setSafeOnClickListener {
            fragmentManager?.popBackStack()
        }

        readBtn?.setSafeOnClickListener {
            fragmentManager?.popBackStack()
        }
    }

    fun initAdapter(){
        val faceControl = AdminPermission("Фейс контроль","Может сканировать входные билеты.")
        val moderator = AdminPermission("Модератор","Может удалять сообщения пользователей, добавлять пользователей в черный список.")
        val editor = AdminPermission("Редактор","Имеет полномочия модератора а так же может изменять описание, аватар группы, управлять информацией о мероприятиях.")
        val administrator = AdminPermission("Администратор","Имеет полномочия редактора а так же может назначать и снимать администраторов")

        val adminPermissionsTypes = arrayListOf<AdminPermission>()

        adminPermissionsTypes.add(faceControl)
        adminPermissionsTypes.add(moderator)
        adminPermissionsTypes.add(editor)
        adminPermissionsTypes.add(administrator)

        typeAdminRv?.layoutManager = LinearLayoutManager(context)
        typeAdminRv?.adapter = AdminPermissionAdapter(object : AdminPermissionListener {
            override fun click() {

            }
        }, adminPermissionsTypes)
    }

    override fun setAvatar(avatar: Bitmap) {
        contactAvatar?.setImageBitmap(avatar)
    }
}
