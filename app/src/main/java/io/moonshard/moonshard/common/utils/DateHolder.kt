package io.moonshard.moonshard.common.utils

import java.text.SimpleDateFormat
import java.util.*

class DateHolder(var isoDate: String) {
    val year: Int
    val month: Int
    val weekOfMonth: Int
    val dayOfMonth: Int
    val dayOfWeek: Int
    val hour: Int
    val minute: Int

    val calendar: Calendar = Calendar.getInstance()

    init {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
        calendar.time = sdf.parse(isoDate)

        year = calendar.get(Calendar.YEAR)
        month = calendar.get(Calendar.MONTH)
        weekOfMonth = calendar.get(Calendar.WEEK_OF_MONTH)
        dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        hour = calendar.get(Calendar.HOUR_OF_DAY)
        minute = calendar.get(Calendar.MINUTE)
    }

    fun getMonthString(month: Int): String {
        return when (month) {
            0 -> {
                "января"
            }
            1 -> {
                "февраля"
            }
            2 -> {
                "марта"
            }
            3 -> {
                "апреля"
            }
            4 -> {
                "мая"
            }
            5 -> {
                "июня"
            }
            6 -> {
                "июля"
            }
            7 -> {
                "августа"
            }
            8 -> {
                "сенятбря"
            }
            9 -> {
                "октября"
            }
            10 -> {
                "ноября"
            }
            11 -> {
                "декабря"
            }
            else -> ""
        }
    }

    fun alreadyComeDate(): Boolean {
        val calendarNow = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
        val isoCalendar = sdf.parse(isoDate)
        return calendarNow.time.after(isoCalendar)

        /*
        //val unixTime = unixTimestamp*1000L
        val unixTime = isoDate*1000L
        val calendar = Calendar.getInstance()
        return unixTime < calendar.timeInMillis

         */
    }
}