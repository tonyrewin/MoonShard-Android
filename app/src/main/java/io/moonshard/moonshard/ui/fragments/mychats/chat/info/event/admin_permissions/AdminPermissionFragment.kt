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
import io.moonshard.moonshard.ui.fragments.mychats.chat.MainChatFragment
import kotlinx.android.synthetic.main.fragment_admin_permission.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import org.jivesoftware.smackx.muc.Affiliate
import org.jivesoftware.smackx.muc.Occupant


class AdminPermissionFragment : MvpAppCompatFragment(),AdminPermissionView {

    @InjectPresenter
    lateinit var presenter: AdminPermissionPresenter

    var idChat: String = ""
    var currentTypeRole:String? = null
    var userJid:String?=null
    var choosedRole = ""

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
            currentTypeRole = it.getString("currentTypeRole")
            userJid = it.getString("userJid")
        }

        presenter.getData(currentTypeRole,userJid)

        initAdapter()

        backBtn?.setSafeOnClickListener {
            parentFragmentManager.popBackStack()
        }

        readyBtn?.setSafeOnClickListener {
            presenter.changeRole(choosedRole,userJid!!,idChat)
        }
    }

    fun initAdapter(){
        val faceControl = AdminPermission("Фейс контроль","Может сканировать входные билеты.")
        val administrator = AdminPermission("Администратор","Имеет полномочия фейс-контрольщика,  а так же может добавлять пользователей в черный список")

        val adminPermissionsTypes = arrayListOf<AdminPermission>()

        adminPermissionsTypes.add(faceControl)
        adminPermissionsTypes.add(administrator)

        typeAdminRv?.layoutManager = LinearLayoutManager(context)
        typeAdminRv?.adapter = AdminPermissionAdapter(object : AdminPermissionListener {
            override fun click(role: String) {
                choosedRole = role
            }
        }, adminPermissionsTypes)
    }

    override fun updateData(type:Int){
        (typeAdminRv?.adapter as? AdminPermissionAdapter)?.update(type)
    }

    override fun setAvatar(avatar: Bitmap) {
        contactAvatar?.setImageBitmap(avatar)
    }

    override fun showNickName(nickName:String){
        nameTv?.text = nickName
    }

  override  fun goToChatScreen(){
        (parentFragment as? MainChatFragment)?.moveAndClearPopBackStackChild()
    }
}
