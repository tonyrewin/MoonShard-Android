package io.moonshard.moonshard.presentation.presenter.profile.history

import android.util.Log
import io.moonshard.moonshard.models.wallet.DateItem
import io.moonshard.moonshard.models.wallet.GeneralItem
import io.moonshard.moonshard.models.wallet.ListItem
import io.moonshard.moonshard.models.wallet.Transaction
import io.moonshard.moonshard.presentation.view.profile.history.HistoryTransactionView
import io.moonshard.moonshard.ui.fragments.profile.history.CustomComparator
import moxy.InjectViewState
import moxy.MvpPresenter
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


@InjectViewState
class HistoryTransactionPresenter : MvpPresenter<HistoryTransactionView>() {


    private var transactions = ArrayList<Transaction>()
    private var fullTransactions = ArrayList<Transaction>()
    private var isFilter = false
    private var isCalendarFilter = false
    private var isTopUpFilter = false
    private var isWriteOffFilter = false

    private var calendar:Calendar?=null


    fun getTransactions() {
        val myOptions: ArrayList<Transaction> = arrayListOf()

        myOptions.add(Transaction("Списание", "2020-05-01", 0))
        myOptions.add(Transaction("Списание", "2020-05-02", 0))
        myOptions.add(Transaction("Списание", "2020-05-03", 0))
        myOptions.add(Transaction("Пополнение", "2020-05-03", 1))
        myOptions.add(Transaction("Списание", "2020-05-17", 0))
        myOptions.add(Transaction("Пополнение", "2020-05-17", 1))
        myOptions.add(Transaction("Списание", "2020-05-17", 0))
        myOptions.add(Transaction("Пополнение", "2020-05-05", 1))
        myOptions.add(Transaction("Списание", "2020-06-17", 0))

        transactions.clear()
        fullTransactions.clear()
        transactions.addAll(myOptions)
        fullTransactions.addAll(myOptions)

        convertTransactionsWithDate(myOptions)
    }

    fun convertTransactionsWithDate(myOptions: ArrayList<Transaction>) {
        // val groupedHashMap: HashMap<String, ArrayList<Transaction>> =
        //    groupDataIntoHashMap(myOptions)

        val groupedHashMap: TreeMap<String, ArrayList<Transaction>> =
            groupDataIntoHashMap(myOptions)

        val consolidatedList: ArrayList<ListItem> = arrayListOf()

        for (date in groupedHashMap.keys) {
            val dateItem = DateItem()
            dateItem.date = date
            consolidatedList.add(dateItem)
            for (pojoOfJsonArray in groupedHashMap[date]!!) {
                val generalItem = GeneralItem()
                generalItem.transaction = pojoOfJsonArray //setBookingDataTabs(bookingDataTabs);
                consolidatedList.add(generalItem)
            }
        }
        viewState?.setData(consolidatedList)
    }

    private fun groupDataIntoHashMap(listOfPojosOfJsonArray: List<Transaction>): TreeMap<String, ArrayList<Transaction>> {
        val groupedHashMap: HashMap<String, ArrayList<Transaction>> =
            HashMap()
        for (pojoOfJsonArray in listOfPojosOfJsonArray) {
            val hashMapKey = pojoOfJsonArray.date
            if (groupedHashMap.containsKey(hashMapKey)) { // The key is already in the HashMap; add the pojo object
// against the existing key.
                groupedHashMap[hashMapKey]!!.add(pojoOfJsonArray)
            } else { // The key is not there in the HashMap; create a new key-value pair
                val list: ArrayList<Transaction> = arrayListOf()
                list.add(pojoOfJsonArray)
                groupedHashMap[hashMapKey] = list
            }
        }

        val comp = CustomComparator()

        val mainTreeMap: TreeMap<String, ArrayList<Transaction>> = TreeMap(comp)
        mainTreeMap.putAll(groupedHashMap)

        return mainTreeMap
    }

    private fun groupDataIntoTreeMap(listOfPojosOfJsonArray: List<Transaction>): TreeMap<String, ArrayList<Transaction>> {
        val comp = CustomComparator()
        val groupedTreeMap: TreeMap<String, ArrayList<Transaction>> =
            TreeMap()
        for (pojoOfJsonArray in listOfPojosOfJsonArray) {
            val hashMapKey = pojoOfJsonArray.date
            if (groupedTreeMap.containsKey(hashMapKey)) { // The key is already in the HashMap; add the pojo object
// against the existing key.
                groupedTreeMap[hashMapKey]!!.add(pojoOfJsonArray)
            } else { // The key is not there in the HashMap; create a new key-value pair
                val list: ArrayList<Transaction> = arrayListOf()
                list.add(pojoOfJsonArray)
                groupedTreeMap[hashMapKey] = list
            }
        }

        val mainTreeMap: TreeMap<String, ArrayList<Transaction>> = TreeMap(comp)
        mainTreeMap.putAll(groupedTreeMap)

        return mainTreeMap
    }

    fun setFilterDate(calendar: Calendar) {
        if (isFilter) {
            val format1 = SimpleDateFormat("yyyy-MM-dd")
            val formatted = format1.format(calendar.time)
            Log.d("myCalendar", formatted)

            val list: List<Transaction> = transactions.filter {
                it.date.contains(formatted)
            }

            transactions.clear()
            transactions.addAll(list)
        } else {
            val format1 = SimpleDateFormat("yyyy-MM-dd")
            val formatted = format1.format(calendar.time)
            Log.d("myCalendar", formatted)

            val list: List<Transaction> = fullTransactions.filter {
                it.date.contains(formatted)
            }

            transactions.clear()
            transactions.addAll(list)
        }
        this.calendar = calendar
        isCalendarFilter = true
        convertTransactionsWithDate(transactions)
    }

    fun disableFilterDate(){
        isCalendarFilter = false
        if(isFilter){
            if(isTopUpFilter){
                topUpFilter(true)
            }else if(isWriteOffFilter){
                writeOffFilter(true)
            }
        }else{
            transactions.clear()
            transactions.addAll(fullTransactions)
            convertTransactionsWithDate(transactions)
        }
    }

    fun topUpFilter(isActive: Boolean) {
        if(isCalendarFilter){
            if (isActive) {
                val list: List<Transaction> = transactions.filter {
                    it.typeTransaction == 1
                }
                transactions.clear()
                transactions.addAll(list)

                convertTransactionsWithDate(transactions)
                isFilter = true
                isTopUpFilter = true
            } else {
                isFilter = false
                setFilterDate(calendar!!)
                isTopUpFilter = false
            }
        }else{
            if (isActive) {
                val list: List<Transaction> = fullTransactions.filter {
                    it.typeTransaction == 1
                }
                transactions.clear()
                transactions.addAll(list)

                convertTransactionsWithDate(transactions)
                isFilter = true
                isTopUpFilter = true
            } else {
                isFilter = false
                isTopUpFilter = false
                transactions.clear()
                transactions.addAll(fullTransactions)
                convertTransactionsWithDate(transactions)
            }
        }
    }

    fun writeOffFilter(isActive: Boolean) {
        if(isCalendarFilter){
            if (isActive) {
                val list: List<Transaction> = transactions.filter {
                    it.typeTransaction == 0
                }

                transactions.clear()
                transactions.addAll(list)

                convertTransactionsWithDate(transactions)
                isFilter = true
                isWriteOffFilter = true

            } else {
                //getTransactions()
                isWriteOffFilter = false
                isFilter = false
                setFilterDate(calendar!!)
            }
        }else{
            if (isActive) {
                val list: List<Transaction> = fullTransactions.filter {
                    it.typeTransaction == 0
                }

                transactions.clear()
                transactions.addAll(list)

                convertTransactionsWithDate(transactions)
                isFilter = true
                isWriteOffFilter = true
            } else {
                isWriteOffFilter = false
                isFilter = false
                transactions.clear()
                transactions.addAll(fullTransactions)
                convertTransactionsWithDate(transactions)
            }
        }
    }
}