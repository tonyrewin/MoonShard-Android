package io.moonshard.moonshard.ui.adapters

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.R
import io.moonshard.moonshard.models.Category
import io.moonshard.moonshard.models.api.RoomPin
import java.util.concurrent.ExecutionException


interface ListChatMapListener {
    fun clickChat(categoryName: String)
}

class ListChatMapAdapter (val listener: ListChatMapListener, private var chats: List<RoomPin>) :
    RecyclerView.Adapter<ListChatMapAdapter.ViewHolder>() {

    var focusedItem = -1


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.chat_map_item,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        setAvatar(chats[position].roomId.toString(),holder.groupIv!!)
        holder.groupNameTv?.text = chats[position].roomId
        holder.valueMembersTv?.text = "1000"
        holder.locationValueTv?.text = calculationByDistance(chats[position].latitude,chats[position].longtitude)


        holder.itemView.setOnClickListener {
            focusedItem = position
            notifyDataSetChanged()
            //listener.clickChat(chats[position].name)
        }
    }

    private fun calculationByDistance(latRoom: String, lngRoom: String): String {
        MainApplication.getCurrentLocation()?.let {
            val myLat = MainApplication.getCurrentLocation().latitude
            val myLng = MainApplication.getCurrentLocation().longitude

            val km = SphericalUtil.computeDistanceBetween(
                LatLng(latRoom.toDouble(), lngRoom.toDouble()),
                LatLng(myLat, myLng)
            ).toInt() / 1000
            return if (km < 1) {
                (SphericalUtil.computeDistanceBetween(
                    LatLng(
                        latRoom.toDouble(),
                        lngRoom.toDouble()
                    ), LatLng(myLat, myLng)
                ).toInt()).toString() + " метрах"
            } else {
                (SphericalUtil.computeDistanceBetween(
                    LatLng(latRoom.toDouble(), lngRoom.toDouble()),
                    LatLng(myLat, myLng)
                ).toInt() / 1000).toString() + " км"
            }
        }
        return ""
    }

    private fun setAvatar(jid: String, imageView: ImageView) {
        var avatarBytes: ByteArray? = ByteArray(0)
        try {
            val future =
                MainApplication.getXmppConnection().network.loadAvatar(jid)

            if (future != null) {
                avatarBytes = future.get()
            }

        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: ExecutionException) {
            e.printStackTrace()
        }

        val avatar: Bitmap?
        if (avatarBytes != null) {
            avatar = BitmapFactory.decodeByteArray(avatarBytes, 0, avatarBytes.size)
            imageView.setImageBitmap(avatar)
        }
    }

    fun setChats(moderators: List<RoomPin>) {
        this.chats = moderators
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = chats.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        internal var groupNameTv: TextView? = view.findViewById(R.id.groupNameTv)
        internal var valueMembersTv: TextView? = view.findViewById(R.id.valueMembersTv)
        internal var locationValueTv: TextView? = view.findViewById(R.id.locationValueTv)
        internal var groupIv: ImageView? = view.findViewById(R.id.profileImage)
        internal var mainLayout: RelativeLayout? = view.findViewById(R.id.mainLayout)
    }
}