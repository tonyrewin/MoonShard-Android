package io.moonshard.moonshard.ui.fragments.profile.history

import java.text.SimpleDateFormat


class CustomComparator:Comparator<String> {

    override fun compare(date1: String, date2: String): Int {

        val format1 = SimpleDateFormat("yyyy-MM-dd")

        val dateOne =format1.parse(date1)
        val dateTwo=format1.parse(date2)

        if(dateOne == dateTwo) return 0

        return if(dateOne.before(dateTwo)){
            1
        }else{
            -1
        }
    }
}