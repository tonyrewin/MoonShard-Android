package io.moonshard.moonshard.common.utils

import java.util.*

class DateHolder(unixTimestamp: Long) {
    val year: Int
    val month: Int
    val weekOfMonth: Int
    val dayOfMonth: Int
    val dayOfWeek: Int
    val hour: Int
    val minute: Int

    val calendar: Calendar = Calendar.getInstance()

    init {
        calendar.timeInMillis = unixTimestamp
        year = calendar.get(Calendar.YEAR)
        month = calendar.get(Calendar.MONTH)
        weekOfMonth = calendar.get(Calendar.WEEK_OF_MONTH)
        dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        hour = calendar.get(Calendar.HOUR_OF_DAY)
        minute = calendar.get(Calendar.MINUTE)
    }
}