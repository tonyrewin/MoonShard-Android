package io.moonshard.moonshard.ui.adapters.chat

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.moonshard.moonshard.R
import io.moonshard.moonshard.common.utils.setSafeOnClickListener
import io.moonshard.moonshard.models.AdminPermission

interface AdminPermissionListener {
    fun click()
}

class AdminPermissionAdapter(
    val listener: AdminPermissionListener,
    private var adminPermission: ArrayList<AdminPermission>
) :
    RecyclerView.Adapter<AdminPermissionAdapter.ViewHolder>() {

    var focusedItem = -1
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (focusedItem == position) {
            holder.mainLayout?.setBackgroundColor(Color.parseColor("#EEF6FF"))
            holder.choosedIv?.visibility = View.VISIBLE
        } else {
            holder.mainLayout?.setBackgroundColor(Color.parseColor("#FFFFFF"))
            holder.choosedIv?.visibility = View.GONE
        }
        holder.type?.text = adminPermission[position].type
        holder.descriptionTv?.text = adminPermission[position].description

        holder.itemView.setSafeOnClickListener {
            focusedItem = position
            notifyDataSetChanged()
            listener.click()
        }
    }

    override fun getItemCount(): Int = adminPermission.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.admin_permission_item,
                parent,
                false
            )
        )

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        internal var type: TextView? = view.findViewById(R.id.nameEvent)
        internal var descriptionTv: TextView? = view.findViewById(R.id.descriptionTv)
        internal var mainLayout: RelativeLayout? = view.findViewById(R.id.mainLayout)
        internal var choosedIv: ImageView? = view.findViewById(R.id.choosedIv)
    }
}