package io.moonshard.moonshard.ui.adapters.wallet

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.moonshard.moonshard.R
import io.moonshard.moonshard.common.utils.setSafeOnClickListener
import io.moonshard.moonshard.models.wallet.DateItem
import io.moonshard.moonshard.models.wallet.GeneralItem
import io.moonshard.moonshard.models.wallet.ListItem

interface TransactionsWalletListener {
    fun click()
}


class TransactionsWalletAdapter(val listener: TransactionsWalletListener,var consolidatedList: ArrayList<ListItem>?) :
    RecyclerView.Adapter<RecyclerView.ViewHolder?>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewHolder: RecyclerView.ViewHolder?
        val inflater = LayoutInflater.from(parent.context)
        when (viewType) {
            ListItem.TYPE_GENERAL -> {
                val v1: View = inflater.inflate(R.layout.transacation_wallet_item, parent, false)
                viewHolder = GeneralViewHolder(v1)
            }
            ListItem.TYPE_DATE -> {
                val v2: View = inflater.inflate(R.layout.date_wallet_item, parent, false)
                viewHolder = DateViewHolder(v2)
            }
            else -> {
                val v2: View = inflater.inflate(R.layout.transacation_wallet_item, parent, false)
                viewHolder = DateViewHolder(v2)
            }
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            ListItem.TYPE_GENERAL -> {
                val generalItem: GeneralItem = consolidatedList!![position] as GeneralItem
                val generalViewHolder = holder as GeneralViewHolder?

                generalViewHolder?.itemView?.setSafeOnClickListener {
                    listener.click()
                }
               generalViewHolder!!.nameTransaction.text = generalItem.transaction.name
            }
            ListItem.TYPE_DATE -> {
                val dateItem: DateItem = consolidatedList!![position] as DateItem
                val dateViewHolder = holder as DateViewHolder?
                dateViewHolder!!.txtTitle.text = dateItem.date
            }
        }
    }

    // ViewHolder for date row item
    internal inner class DateViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var txtTitle: TextView = v.findViewById(R.id.dateTv)

    }

    // View holder for general row item
    internal inner class GeneralViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var nameTransaction: TextView = v.findViewById(R.id.nameTransaction)
    }

    override fun getItemViewType(position: Int): Int {
        return consolidatedList!![position].type
    }

    override fun getItemCount(): Int {
        return if (consolidatedList != null) consolidatedList!!.size else 0
    }

    fun setTransaction(transitions: List<ListItem>) {
        this.consolidatedList?.clear()
        this.consolidatedList?.addAll(transitions)
        notifyDataSetChanged()
    }

    init {
        this.consolidatedList = consolidatedList
    }
}