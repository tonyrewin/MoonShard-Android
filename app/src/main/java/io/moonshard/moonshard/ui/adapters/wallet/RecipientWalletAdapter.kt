package io.moonshard.moonshard.ui.adapters.wallet

import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.moonshard.moonshard.R


interface RecipientWalletListener {
    fun click()
}

class RecipientWalletAdapter(val listener: RecipientWalletListener, private var tickets: ArrayList<String>) :
    RecyclerView.Adapter<RecipientWalletAdapter.ViewHolder>()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.recipient_item,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        //todo hardcore
        if(position==9){
            holder.viewLine?.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = 10

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        internal var checkBox:ImageView = view.findViewById(R.id.checkBox)
        internal var avatar: ImageView? = view.findViewById(R.id.userAdminAvatar)
        internal var statusTv: TextView? = view.findViewById(R.id.statusTv)
        internal var nameAdminTv: TextView? = view.findViewById(R.id.nameAdminTv)
        internal var viewLine: View? = view.findViewById(R.id.viewLine)
    }
}