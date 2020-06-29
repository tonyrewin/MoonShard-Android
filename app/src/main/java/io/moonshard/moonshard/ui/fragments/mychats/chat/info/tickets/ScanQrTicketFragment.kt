package io.moonshard.moonshard.ui.fragments.mychats.chat.info.tickets

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import com.google.zxing.BarcodeFormat
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.BarcodeEncoder
import io.moonshard.moonshard.R
import io.moonshard.moonshard.common.utils.setSafeOnClickListener
import io.moonshard.moonshard.models.wallet.QrCodeModel
import io.moonshard.moonshard.presentation.presenter.chat.info.tickets.ScanQrTicketPresenter
import io.moonshard.moonshard.presentation.view.chat.info.tickets.ScanQrTicketView
import kotlinx.android.synthetic.main.fragment_scan_qr_ticket.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import java.io.File


class ScanQrTicketFragment : MvpAppCompatFragment(),
    ScanQrTicketView {

    @InjectPresenter
    lateinit var presenter: ScanQrTicketPresenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_scan_qr_ticket, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initScan()

        backBtn?.setSafeOnClickListener {
            parentFragmentManager.popBackStack()
        }

        readyBtn?.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun initScan() {
        val integrator = IntentIntegrator.forSupportFragment(this)
        integrator.setBarcodeImageEnabled(true)
        integrator.initiateScan()
    }

    override fun alreadyWasScan(error: String, typeTicketName: String) {
        //  setImageFromImagePath(barcodeImagePath)
        //qrLayout?.visibility = View.VISIBLE
        layoutScan?.visibility = View.VISIBLE
        layoutScan?.setBackgroundResource(R.drawable.layout_red_corner_bg)
        nameTicket?.setTextColor(Color.parseColor("#FFFFFF"))
        typeTicket?.setTextColor(Color.parseColor("#FFFFFF"))
        scanOk?.visibility = View.GONE
        nameTicket?.text = error
        typeTicket?.text = typeTicketName
    }

    override fun showSuccessScannedTicket(
        ticket: QrCodeModel,
        typeTicketName: String
    ) {
        typeTicket?.text = typeTicketName.toString()
        layoutScan?.visibility = View.VISIBLE
        layoutScan?.setBackgroundResource(R.drawable.layout_settings_bg)
        nameTicket?.setTextColor(Color.parseColor("#333333"))
        typeTicket?.setTextColor(Color.parseColor("#9B9BB6"))
        scanOk?.visibility = View.VISIBLE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result =
            IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                fragmentManager?.popBackStack()
            } else {
                successScan(result.contents)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun successScan(ticketJson: String) {
        Log.d("scanQrCodeTest , json: ",ticketJson)

        val ticket = Gson() .fromJson(ticketJson, QrCodeModel::class.java)
        presenter.scan(ticket)
    }

    fun generateQrCode(content: String) {
        try {
            val barcodeEncoder = BarcodeEncoder()
            val bitmap =
                barcodeEncoder.encodeBitmap(content, BarcodeFormat.QR_CODE, 200, 200)
            qrCode.setImageBitmap(bitmap)
        } catch (e: Exception) {
        }
    }

    fun setImageFromImagePath(path: String) {
        val imgFile = File(path)
        if (imgFile.exists()) {
            val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
            qrCode.setImageBitmap(myBitmap)
        }
    }

    override fun showProgressBar(){
        progressBar?.visibility = View.VISIBLE
    }

    override fun hideProgressBar(){
        progressBar?.visibility = View.GONE
    }
}
