package io.moonshard.moonshard.ui.activities.onboardregistration

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import io.moonshard.moonshard.R
import io.moonshard.moonshard.StreamUtil
import io.moonshard.moonshard.presentation.presenter.StartProfilePresenter
import io.moonshard.moonshard.presentation.view.StartProfileView
import io.moonshard.moonshard.ui.activities.MainActivity
import kotlinx.android.synthetic.main.activity_start_profile.*
import moxy.MvpAppCompatActivity
import moxy.presenter.InjectPresenter
import java.io.BufferedInputStream
import java.io.ByteArrayInputStream
import java.net.URLConnection

class StartProfileActivity : MvpAppCompatActivity(), StartProfileView {

    var bytes: ByteArray? = null
    var mimeType: String? = null

    @InjectPresenter
    lateinit var presenter: StartProfilePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_profile)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

        nickNameEt?.setText(presenter.getNickName())

        avatarIv?.setOnClickListener {
            chooseFile()
        }

        changeAvatarTv?.setOnClickListener {
            chooseFile()
        }

        startBtn?.setOnClickListener {
            presenter.setData(nickNameEt.text.toString(),bytes,mimeType)
        }

        clearNickTv?.setOnClickListener {
            nickNameEt.text.clear()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val uri = data?.data

        if (uri != null) {
            val input = contentResolver?.openInputStream(uri)
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
        changeAvatarTv?.visibility = View.VISIBLE
        avatarIv?.setImageBitmap(avatar)
    }

    private fun getMimeType(data: ByteArray): String? {
        return try {
            val inputStream = BufferedInputStream(ByteArrayInputStream(data))
            URLConnection.guessContentTypeFromStream(inputStream)
        } catch (e: Exception) {
            null
        }
    }

    private fun chooseFile() {
        var choosePhoto = Intent(Intent.ACTION_GET_CONTENT)
        choosePhoto.type = "image/*"
        choosePhoto = Intent.createChooser(choosePhoto, "Select Picture")
        startActivityForResult(choosePhoto, 1)
    }

    override fun showContactsScreen() {
        val intentContactsActivity = Intent(
            this,
            MainActivity::class.java
        )
        startActivity(intentContactsActivity)
    }

    override fun showError(error: String) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
    }
}
