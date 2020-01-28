package io.moonshard.moonshard.ui.fragments.settings

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import io.moonshard.moonshard.StreamUtil
import io.moonshard.moonshard.presentation.presenter.settings.ChangeProfilePresenter
import io.moonshard.moonshard.presentation.view.settings.ChangeProfileView
import kotlinx.android.synthetic.main.fragment_change_profile.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import java.io.BufferedInputStream
import java.io.ByteArrayInputStream
import java.net.URLConnection
import android.graphics.BitmapFactory
import io.moonshard.moonshard.common.utils.setSafeOnClickListener
import java.lang.Exception


class ChangeProfileFragment : MvpAppCompatFragment(), ChangeProfileView {

    @InjectPresenter
    lateinit var presenter: ChangeProfilePresenter

    var bytes: ByteArray? = null
    var mimeType: String? = null

    override fun setData(nickName: String?, description: String?) {
        nameTv?.setText(nickName ?: "")
        descriptionTv?.setText(description ?: "")
    }

    override fun setAvatar(avatar: Bitmap?) {
        avatarIv?.setImageBitmap(avatar)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(
            io.moonshard.moonshard.R.layout.fragment_change_profile,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.getInfoProfile()
        presenter.getAvatar()

        readyBtn?.setSafeOnClickListener {
            presenter.setData(
                nameTv?.text.toString(),
                descriptionTv?.text.toString(),
                bytes,
                mimeType
            )
        }

        backBtn?.setSafeOnClickListener {
            fragmentManager?.popBackStack()
        }

        avatarIv?.setSafeOnClickListener {
            chooseFile()
        }
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
        }catch (e:Exception){
            null
        }
    }

    override fun showProfile() {
        fragmentManager?.popBackStack()
        fragmentManager?.popBackStack()
    }

    override fun showError(error: String) {
        Toast.makeText(activity, error, Toast.LENGTH_SHORT).show()
    }
}
