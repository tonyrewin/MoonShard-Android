package io.moonshard.moonshard.ui.fragments.mychats.chat.info.chat

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import io.moonshard.moonshard.R
import io.moonshard.moonshard.StreamUtil
import io.moonshard.moonshard.common.utils.setSafeOnClickListener
import io.moonshard.moonshard.db.ChangeEventRepository
import io.moonshard.moonshard.presentation.presenter.chat.info.ManageChatPresenter
import io.moonshard.moonshard.presentation.view.chat.ManageChatView
import io.moonshard.moonshard.ui.fragments.mychats.chat.MainChatFragment
import kotlinx.android.synthetic.main.fragment_manage_chat.*
import kotlinx.android.synthetic.main.fragment_manage_chat.adminsCountTv
import kotlinx.android.synthetic.main.fragment_manage_chat.adminsLayout
import kotlinx.android.synthetic.main.fragment_manage_chat.avatarIv
import kotlinx.android.synthetic.main.fragment_manage_chat.backBtn
import kotlinx.android.synthetic.main.fragment_manage_chat.descriptionEt
import kotlinx.android.synthetic.main.fragment_manage_chat.destroyRoom
import kotlinx.android.synthetic.main.fragment_manage_chat.membersLayout
import kotlinx.android.synthetic.main.fragment_manage_chat.nameEt
import kotlinx.android.synthetic.main.fragment_manage_chat.occupantsCountTv
import kotlinx.android.synthetic.main.fragment_manage_chat.progressBar
import kotlinx.android.synthetic.main.fragment_manage_chat.readyBtn
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import java.io.BufferedInputStream
import java.io.ByteArrayInputStream
import java.net.URLConnection


class ManageChatFragment : MvpAppCompatFragment(), ManageChatView {

    @InjectPresenter
    lateinit var presenter: ManageChatPresenter
    var idChat = ""
    var bytes: ByteArray? = null
    var mimeType: String? = null
    var stringByteAvatar:String? = null

    var typeRole = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_manage_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            idChat = it.getString("chatId")
            typeRole = it.getString("typeRole")
            presenter.getDataInfo(idChat)
        }

        when (typeRole) {
            "owner" -> {
                readyBtn?.setSafeOnClickListener {
                    presenter.setData(nameEt.text.toString(), descriptionEt.text.toString(), idChat,bytes,mimeType)
                }
            }
            "admin" -> {
                avatarIv?.isEnabled = false
                nameEt?.isEnabled = false
                descriptionEt?.isEnabled = false
                adminsLayout?.isEnabled = false
                destroyRoom?.isEnabled = false
                readyBtn?.setSafeOnClickListener {
                    showChatInfo()
                }
            }
            "FaceController" -> {
                avatarIv?.isEnabled = false
                nameEt?.isEnabled = false
                descriptionEt?.isEnabled = false
                membersLayout?.isEnabled = false
                adminsLayout?.isEnabled = false
                destroyRoom?.isEnabled = false
                readyBtn?.setSafeOnClickListener {
                    showChatInfo()
                }
            }
        }

        membersLayout?.setSafeOnClickListener {
            showMembersScreen()
        }

        adminsLayout?.setSafeOnClickListener {
            showAdminsScreen()
        }

        backBtn?.setSafeOnClickListener {
            fragmentManager?.popBackStack()
        }

        avatarIv?.setSafeOnClickListener {
            chooseFile()
        }

        destroyRoom?.setOnClickListener {
            presenter.destroyRoom(idChat)
        }
    }

    private fun showAdminsScreen() {
        (parentFragment as? MainChatFragment)?.showAdminsScreen(idChat)
    }

    private fun showMembersScreen() {
        (parentFragment as? MainChatFragment)?.showMembersScreen(idChat,typeRole)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val uri = data?.data

        if (uri != null) {
            val input = context?.contentResolver?.openInputStream(uri)
            if (input != null) {
                val file = StreamUtil.stream2file(input)
                bytes = file.readBytes()

                bytes?.let {
                    mimeType = getMimeType(it)
                    val bitmap = BitmapFactory.decodeFile(file.path)
                  //  stringByteAvatar = bitMapToString(bitmap)
                    setAvatar(bitmap)
                }
            }
        }
    }

    override fun setAvatar(avatar: Bitmap?) {
        avatarIv?.setImageBitmap(avatar)
    }

    private fun chooseFile() {
        var choosePhoto = Intent(Intent.ACTION_GET_CONTENT)
        choosePhoto.type = "image/*"
        choosePhoto = Intent.createChooser(choosePhoto, "Select Picture")
        startActivityForResult(choosePhoto, 1)
    }

    private fun getMimeType(data: ByteArray): String? {
        return try {
            val inputStream = BufferedInputStream(ByteArrayInputStream(data))
            URLConnection.guessContentTypeFromStream(inputStream)
        } catch (e: Exception) {
            null
        }
    }

    override fun showName(name: String?) {
        nameEt?.setText(name)
    }

    override fun showDescription(description: String) {
        descriptionEt?.setText(description)
    }

    override fun showChatInfo() {
        activity!!.supportFragmentManager.popBackStack()
    }

    override fun showToast(text: String) {
        Toast.makeText(context!!, text, Toast.LENGTH_SHORT).show()
    }

    override fun showOccupantsCount(text: String) {
        occupantsCountTv?.text = text
    }

    override fun showAdminsCount(text: String) {
        adminsCountTv?.text = text
    }

    override fun showProgressBar() {
        progressBar?.visibility = View.VISIBLE
    }

    override fun hideProgressBar() {
        progressBar?.visibility = View.GONE
    }

    override fun showChatsScreen(){
        (parentFragment as? MainChatFragment)?.moveAndClearPopBackStack()
    }
}
