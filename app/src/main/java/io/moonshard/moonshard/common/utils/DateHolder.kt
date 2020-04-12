package io.moonshard.moonshard.common.utils

import java.util.*

class DateHolder(var unixTimestamp: Long) {
    val year: Int
    val month: Int
    val weekOfMonth: Int
    val dayOfMonth: Int
    val dayOfWeek: Int
    val hour: Int
    val minute: Int

    val calendar: Calendar = Calendar.getInstance()
    val locale = Locale.getDefault()
    
    init {
        calendar.timeInMillis = unixTimestamp*1000L
        year = calendar.get(Calendar.YEAR)
        month = calendar.get(Calendar.MONTH)
        weekOfMonth = calendar.get(Calendar.WEEK_OF_MONTH)
        dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        hour = calendar.get(Calendar.HOUR_OF_DAY)
        minute = calendar.get(Calendar.MINUTE)
    }

    fun getMonthString(month: Int):String {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.MONTH, month)
        return calendar.getDisplayName(Calendar.MONTH, Calendar.LONG_FORMAT, locale)
    }

    fun alreadyComeDate():Boolean{
        val unixTime = unixTimestamp*1000L
        val calendar = Calendar.getInstance()
        return unixTime < calendar.timeInMillis
    }
}