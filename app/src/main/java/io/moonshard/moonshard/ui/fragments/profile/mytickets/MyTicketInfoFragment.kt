package io.moonshard.moonshard.ui.fragments.profile.mytickets

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.moonshardwallet.MainService
import com.example.moonshardwallet.models.Ticket
import com.google.gson.Gson
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.orhanobut.logger.Logger
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.R
import io.moonshard.moonshard.models.wallet.QrCodeModel
import io.moonshard.moonshard.presentation.presenter.profile.mytickets.MyTicketInfoPresenter
import io.moonshard.moonshard.presentation.view.profile.my_tickets.MyTicketInfoView
import kotlinx.android.synthetic.main.fragment_my_ticket_info.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter


class MyTicketInfoFragment: MvpAppCompatFragment(), MyTicketInfoView {

    @InjectPresenter
    lateinit var presenter: MyTicketInfoPresenter

    var isActiveAction: Boolean = false

    var ticket:Ticket? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_ticket_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            val ticketJson = it.getString("ticket")
            ticket  = Gson().fromJson(ticketJson,Ticket::class.java)
            presenter.getEventInfo(ticket!!.jidEvent)
            showInfo(ticket!!)
        }

        backBtn?.setOnClickListener {
            if (isActiveAction) {
                isActiveAction = false
                qrCode?.visibility = View.VISIBLE
               // eventEndedTv?.visibility = View.VISIBLE
                actionBtn?.visibility = View.VISIBLE

                actionLayout?.visibility = View.GONE
                deleteTicketLayout?.visibility = View.GONE
            } else {
                fragmentManager?.popBackStack()
            }
        }

        actionBtn?.setOnClickListener {
            isActiveAction = true
            qrCode?.visibility = View.GONE
           //eventEndedTv?.visibility = View.GONE
            actionBtn?.visibility = View.GONE

            actionLayout?.visibility = View.VISIBLE
            deleteTicketLayout?.visibility = View.VISIBLE
        }
    }

    fun showInfo(ticket:Ticket){
        typeTicket?.text = ticket?.ticketType.toString()
        numberTicket?.text = ticket?.ticketId.toString()

        if(ticket?.payState?.toInt()==2){
            scannedIv?.visibility = View.VISIBLE
        }else{
           scannedIv?.visibility = View.GONE
        }

        Log.d("ticketState",ticket.payState.toString())
        Log.d("ticketId",ticket.ticketId.toString())


        val contentQrCode = QrCodeModel(ticket?.ticketId!!,MainService.getWalletService().myAddress,ticket.jidEvent,ticket.ticketType,ticket.ticketSaleAddress)
        val contentQrCodeJson = Gson().toJson(contentQrCode)
        generateQrCode(contentQrCodeJson)
    }

    fun generateQrCode(content:String){
        try {
            val barcodeEncoder = BarcodeEncoder()
            val bitmap =
                barcodeEncoder.encodeBitmap(content, BarcodeFormat.QR_CODE, 200, 200)
            qrCode.setImageBitmap(bitmap)
        } catch (e: Exception) {
            Logger.d(e)
        }
    }

    override fun showEventInfo(name:String){
        labelTicket?.text = name
    }

    override fun setAvatar(avatar: Bitmap?) {
        MainApplication.getMainUIThread().post {
            avatarTicket?.setImageBitmap(avatar)
        }
    }
}
