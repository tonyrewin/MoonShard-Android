package io.moonshard.moonshard.ui.fragments.profile.wallet.transfer

import android.graphics.Bitmap
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import io.moonshard.moonshard.R
import io.moonshard.moonshard.common.utils.setSafeOnClickListener
import io.moonshard.moonshard.presentation.presenter.profile.wallet.transfer.TransferWalletPresenter
import io.moonshard.moonshard.presentation.view.profile.wallet.transfer.TransferWalletView
import kotlinx.android.synthetic.main.fragment_transfer_wallet.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter


class TransferWalletFragment : MvpAppCompatFragment(),
    TransferWalletView {

    @InjectPresenter
    lateinit var presenter: TransferWalletPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //important for edit text
        activity!!.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_transfer_wallet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        backBtn?.setSafeOnClickListener {
            fragmentManager?.popBackStack()
        }

        moneyValue.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                if (s.isEmpty()) {
                    nextBtn.setBackgroundResource(R.drawable.ic_fill_up_disable)
                } else {
                    nextBtn.setBackgroundResource(R.drawable.ic_fill_up_enable)
                }
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
            }
        })

        chooseMember?.setOnClickListener {
            val addPhotoBottomDialogFragment = TransferRecipientDialogFragment()
            addPhotoBottomDialogFragment.show(
                activity!!.supportFragmentManager,
                "TransferRecipientDialogFragment"
            )
        }
    }

    fun showRecipient(jid: String) {
        presenter.showRecipient(jid)
        //userAdminAvatar.setImageBitmap(avatar)
        // nameAdminTv?.text = name
        //  statusTv?.text = status
    }

    override fun setDataRecipient(name: String, status: String) {
        recipientLayout?.visibility = View.VISIBLE
        nameAdminTv?.text = name
        statusTv?.text = status
    }

    override fun showAvatarRecipient(avatar: Bitmap) {
        userAdminAvatar.setImageBitmap(avatar)
    }

    override fun showToast(text: String) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }
}
