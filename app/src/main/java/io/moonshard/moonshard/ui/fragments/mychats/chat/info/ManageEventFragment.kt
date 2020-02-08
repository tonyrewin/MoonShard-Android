package io.moonshard.moonshard.ui.fragments.mychats.chat.info

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import io.moonshard.moonshard.R
import io.moonshard.moonshard.StreamUtil
import io.moonshard.moonshard.common.utils.setSafeOnClickListener
import io.moonshard.moonshard.presentation.presenter.chat.info.ManageEventPresenter
import io.moonshard.moonshard.presentation.view.chat.info.ManageEventView
import io.moonshard.moonshard.ui.fragments.mychats.chat.MainChatFragment
import kotlinx.android.synthetic.main.fragment_manage_event.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import java.io.BufferedInputStream
import java.io.ByteArrayInputStream
import java.lang.Exception
import java.net.URLConnection


class ManageEventFragment : MvpAppCompatFragment(),ManageEventView {

    @InjectPresenter
    lateinit var presenter: ManageEventPresenter

    var idChat = ""
    var bytes: ByteArray? = null
    var mimeType: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manage_event, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            idChat = it.getString("chatId")
        }

        membersLayout?.setSafeOnClickListener {
            showMembersScreen()
        }

        adminsLayout?.setSafeOnClickListener {
            showAdminsScreen()
        }

        readBtn?.setSafeOnClickListener {
            presenter.setNewNameChat(nameEt.text.toString(), idChat)
            //presenter.setAvatar(idChat,bytes,mimeType)
        }

        backBtn?.setSafeOnClickListener {
            fragmentManager?.popBackStack()
        }

        avatarIv?.setSafeOnClickListener {
            chooseFile()
        }
    }

    private fun showAdminsScreen() {

        ( parentFragment as? MainChatFragment)?.showAddAdminScreen(idChat)

/*
        val bundle = Bundle()
        bundle.putString("chatId", idChat)
        val fragment = AdminsFragment()
        fragment.arguments = bundle
        val ft = activity?.supportFragmentManager?.beginTransaction()
        ft?.replace(R.id.mainContainer, fragment, "AdminsFragment")?.hide(this)
            ?.addToBackStack("AdminsFragment")
            ?.commit()

 */
    }

    private fun showMembersScreen() {

        ( parentFragment as? MainChatFragment)?.showMembersScreen(idChat)

        /*
        val bundle = Bundle()
        bundle.putString("chatId", idChat)
        val fragment = MembersChatFragment()
        fragment.arguments = bundle
        val ft = activity?.supportFragmentManager?.beginTransaction()
        ft?.replace(R.id.mainContainer, fragment, "MembersChatFragment")?.hide(this)
            ?.addToBackStack("MembersChatFragment")
            ?.commit()

         */
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
        }catch (e: Exception){
            null
        }
    }
}
