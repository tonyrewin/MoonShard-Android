package io.moonshard.moonshard.ui.fragments.mychats.chat.info

import android.app.DatePickerDialog
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
import io.moonshard.moonshard.presentation.presenter.chat.info.ManageEventPresenter
import io.moonshard.moonshard.presentation.view.chat.info.ManageEventView
import io.moonshard.moonshard.ui.fragments.mychats.chat.MainChatFragment
import kotlinx.android.synthetic.main.fragment_manage_event.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import java.io.BufferedInputStream
import java.io.ByteArrayInputStream
import java.net.URLConnection
import java.util.*


class ManageEventFragment : MvpAppCompatFragment(), ManageEventView {

    @InjectPresenter
    lateinit var presenter: ManageEventPresenter

    var idChat = ""
    var bytes: ByteArray? = null
    var mimeType: String? = null
    private val dateAndTime = Calendar.getInstance()

    // установка обработчика выбора даты
    var d: DatePickerDialog.OnDateSetListener =
        DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            dateAndTime.set(Calendar.YEAR, year)
            dateAndTime.set(Calendar.MONTH, monthOfYear)
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            setDate(dayOfMonth, monthOfYear)
        }

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
            presenter.getDataInfo(idChat)
        }

        membersLayout?.setSafeOnClickListener {
            showMembersScreen()
        }

        adminsLayout?.setSafeOnClickListener {
            showAdminsScreen()
        }

        readBtn?.setSafeOnClickListener {
            presenter.setData(nameEt.text.toString(), descriptionEt.text.toString(), idChat,bytes,mimeType)
        }

        backBtn?.setSafeOnClickListener {
            fragmentManager?.popBackStack()
        }

        avatarIv?.setSafeOnClickListener {
            chooseFile()
        }

        dateStartEvent?.setOnClickListener {
            DatePickerDialog(
                activity!!, d,
                dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH)
            )
                .show()
        }
    }

    private fun setDate(dayOfMonth: Int, month: Int) {
        when (month) {
            0 -> {
                dateTv.text = "$dayOfMonth января"
            }
            1 -> {
                dateTv.text = "$dayOfMonth февраля"

            }
            2 -> {
                dateTv.text = "$dayOfMonth марта"

            }
            3 -> {
                dateTv.text = "$dayOfMonth апреля"

            }
            4 -> {
                dateTv.text = "$dayOfMonth мая"

            }
            5 -> {
                dateTv.text = "$dayOfMonth июня"

            }
            6 -> {
                dateTv.text = "$dayOfMonth июля"

            }
            7 -> {
                dateTv.text = "$dayOfMonth августа"

            }
            8 -> {
                dateTv.text = "$dayOfMonth сенятбря"

            }
            9 -> {
                dateTv.text = "$dayOfMonth октября"

            }
            10 -> {
                dateTv.text = "$dayOfMonth ноября"
            }
            11 -> {
                dateTv.text = "$dayOfMonth декабря"

            }
        }
    }

    private fun showAdminsScreen() {
        (parentFragment as? MainChatFragment)?.showAddAdminScreen(idChat)
    }

    private fun showMembersScreen() {
        (parentFragment as? MainChatFragment)?.showMembersScreen(idChat)
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
}
