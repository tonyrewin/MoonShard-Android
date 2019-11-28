package io.moonshard.moonshard.ui.adapters

import androidx.recyclerview.widget.RecyclerView
import moxy.MvpDelegate


abstract class MvpBaseAdapter<T : RecyclerView.ViewHolder?>(
    parentDelegate: MvpDelegate<*>,
    childId: String
) :
    RecyclerView.Adapter<T>() {
    private var adapterDelegate: MvpDelegate<out MvpBaseAdapter<T>>

    init {
        @Suppress("LeakingThis")
        adapterDelegate = MvpDelegate(this)
        adapterDelegate.setParentDelegate(parentDelegate, childId)
    }
}