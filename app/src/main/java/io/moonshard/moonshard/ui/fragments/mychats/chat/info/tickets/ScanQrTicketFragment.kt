package io.moonshard.moonshard.ui.fragments.mychats.chat.info.tickets

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.zxing.BarcodeFormat
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.BarcodeEncoder
import io.moonshard.moonshard.R
import io.moonshard.moonshard.common.utils.setSafeOnClickListener
import kotlinx.android.synthetic.main.fragment_scan_qr_ticket.*
import java.io.File


class ScanQrTicketFragment : Fragment() {

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

    private fun initScan(){
        val integrator = IntentIntegrator.forSupportFragment(this)
        integrator.setBarcodeImageEnabled(true)
        integrator.initiateScan()
    }

    private fun alreadyWasScan(barcodeImagePath: String){
        //qrLayout?.visibility = View.VISIBLE
        layoutScan?.visibility = View.VISIBLE
        layoutScan?.setBackgroundResource(R.drawable.layout_red_corner_bg)
        nameTicket?.setTextColor(Color.parseColor("#FFFFFF"))
        typeTicket?.setTextColor(Color.parseColor("#FFFFFF"))
        scanOk?.visibility = View.GONE
      //  setImageFromImagePath(barcodeImagePath)
    }

    private fun successScan(barcodeImagePath: String) {
      //  qrLayout?.visibility = View.VISIBLE
        layoutScan?.visibility = View.VISIBLE
        layoutScan?.setBackgroundResource(R.drawable.layout_settings_bg)
        nameTicket?.setTextColor(Color.parseColor("#333333"))
        typeTicket?.setTextColor(Color.parseColor("#9B9BB6"))
        scanOk?.visibility = View.VISIBLE
       // generateQrCode(barcodeImagePath)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result =
            IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                fragmentManager?.popBackStack()
            } else {
                //  Toast.makeText(activity!!, "Cancelled", Toast.LENGTH_LONG).show()
                //                alreadyWasScan()

                Log.d("test",result.contents)
                Toast.makeText(activity!!, "Scanned: " + result.contents, Toast.LENGTH_LONG).show()
                Log.d("test",result.barcodeImagePath)
                successScan(result.contents)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    fun generateQrCode(content:String){
        try {
            val barcodeEncoder = BarcodeEncoder()
            val bitmap =
                barcodeEncoder.encodeBitmap(content, BarcodeFormat.QR_CODE, 200, 200)
            qrCode.setImageBitmap(bitmap)
        } catch (e: Exception) {
        }
    }

    fun setImageFromImagePath(path:String){
        val imgFile = File(path)
        if (imgFile.exists()) {
            val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
            qrCode.setImageBitmap(myBitmap)
        }
    }
}
