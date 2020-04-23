package io.moonshard.moonshard.ui.fragments.profile.wallet

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import io.moonshard.moonshard.R
import io.moonshard.moonshard.common.utils.setSafeOnClickListener
import io.moonshard.moonshard.models.wallet.DateItem
import io.moonshard.moonshard.models.wallet.GeneralItem
import io.moonshard.moonshard.models.wallet.ListItem
import io.moonshard.moonshard.models.wallet.PojoOfJsonArray
import io.moonshard.moonshard.ui.activities.MainActivity
import io.moonshard.moonshard.ui.adapters.wallet.TransactionsWalletAdapter
import io.moonshard.moonshard.ui.adapters.wallet.TransactionsWalletListener
import kotlinx.android.synthetic.main.fragment_wallet.*


class WalletFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_wallet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).hideBottomNavigationBar()

        initFilterBtn()

        backBtn?.setSafeOnClickListener {
            fragmentManager?.popBackStack()
        }

        fillUpWalletBtn?.setSafeOnClickListener {
            (activity as MainActivity).showFillUpWalletFragment()
        }

        withDrawBtn?.setSafeOnClickListener {
            (activity as MainActivity).showWithdrawWalletFragment()
        }


        transferLayout?.setOnClickListener {
            (activity as MainActivity).showTransferWalletFragment()
        }
        initAdapter()
    }

    fun initFilterBtn(){
        topUpWalletBtn?.setOnClickListener {
            if(topUpWalletBtn.background.constantState == ContextCompat.getDrawable(context!!, R.drawable.wallet_button_active)?.constantState){
                topUpWalletBtn?.setBackgroundResource(R.drawable.wallet_button_default)
                topUpWalletTv.setTextColor(Color.parseColor("#333333"))
            }else{
                writeOffBtn?.setBackgroundResource(R.drawable.wallet_button_default)
                writeOffTv.setTextColor(Color.parseColor("#333333"))

                topUpWalletBtn?.setBackgroundResource(R.drawable.wallet_button_active)
                topUpWalletTv.setTextColor(Color.parseColor("#FFFFFF"))
            }
        }

        writeOffBtn?.setOnClickListener {
            if(writeOffBtn.background.constantState == ContextCompat.getDrawable(context!!, R.drawable.wallet_button_active)?.constantState){
                writeOffBtn?.setBackgroundResource(R.drawable.wallet_button_default)
                writeOffTv.setTextColor(Color.parseColor("#333333"))
            }else{
                topUpWalletBtn?.setBackgroundResource(R.drawable.wallet_button_default)
                topUpWalletTv.setTextColor(Color.parseColor("#333333"))

                writeOffBtn?.setBackgroundResource(R.drawable.wallet_button_active)
                writeOffTv.setTextColor(Color.parseColor("#FFFFFF"))

            }
        }
    }

    fun initAdapter(){
        val myOptions: ArrayList<PojoOfJsonArray> = arrayListOf()
        val consolidatedList: ArrayList<ListItem> = arrayListOf()

        myOptions.add(PojoOfJsonArray("name 1", "2016-06-21"))
        myOptions.add(PojoOfJsonArray("name 2", "2016-06-05"))
        myOptions.add(PojoOfJsonArray("name 2", "2016-06-05"))
        myOptions.add(PojoOfJsonArray("name 3", "2016-05-17"))
        myOptions.add(PojoOfJsonArray("name 3", "2016-05-17"))
        myOptions.add(PojoOfJsonArray("name 3", "2016-05-17"))
        myOptions.add(PojoOfJsonArray("name 3", "2016-05-17"))
        myOptions.add(PojoOfJsonArray("name 2", "2016-06-05"))
        myOptions.add(PojoOfJsonArray("name 3", "2016-05-17"))

        val groupedHashMap: HashMap<String, ArrayList<PojoOfJsonArray>> =
            groupDataIntoHashMap(myOptions)


        for (date in groupedHashMap.keys) {
            val dateItem = DateItem()
            dateItem.date = date
            consolidatedList.add(dateItem)
            for (pojoOfJsonArray in groupedHashMap[date]!!) {
                val generalItem = GeneralItem()
                generalItem.pojoOfJsonArray = pojoOfJsonArray //setBookingDataTabs(bookingDataTabs);
                consolidatedList.add(generalItem)
            }
        }

        rv?.layoutManager = LinearLayoutManager(context)
        rv?.adapter = TransactionsWalletAdapter(object : TransactionsWalletListener{
            override fun click() {
                val addPhotoBottomDialogFragment =
                    AddPhotoBottomDialogFragment()
                addPhotoBottomDialogFragment.show(activity!!.supportFragmentManager, "add_photo_dialog_fragment")
            }
        },consolidatedList)
    }

    private fun groupDataIntoHashMap(listOfPojosOfJsonArray: List<PojoOfJsonArray>): HashMap<String, ArrayList<PojoOfJsonArray>> {
        val groupedHashMap: HashMap<String, ArrayList<PojoOfJsonArray>> =
            HashMap()
        for (pojoOfJsonArray in listOfPojosOfJsonArray) {
            val hashMapKey = pojoOfJsonArray.date
            if (groupedHashMap.containsKey(hashMapKey)) { // The key is already in the HashMap; add the pojo object
// against the existing key.
                groupedHashMap[hashMapKey]!!.add(pojoOfJsonArray)
            } else { // The key is not there in the HashMap; create a new key-value pair
                val list: ArrayList<PojoOfJsonArray> = arrayListOf()
                list.add(pojoOfJsonArray)
                groupedHashMap[hashMapKey] = list
            }
        }
        return groupedHashMap
    }
}
