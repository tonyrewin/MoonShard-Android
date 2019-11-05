package io.moonshard.moonshard.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.moonshard.moonshard.R


interface RvListener {
    fun clickManager(driver:String)
}

class MyChatsAdapter(val listener: RvListener, private val chats: List<String>) : RecyclerView.Adapter<MyChatsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.activity_jitsi_meet, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    }

    override fun getItemCount(): Int = chats.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        //internal var fullNameDriverTv: TextView? = view.findViewById(R.id.fullNameDriver)
       // internal var phoneTv: TextView? = view.findViewById(R.id.phoneDriver)
    }
}