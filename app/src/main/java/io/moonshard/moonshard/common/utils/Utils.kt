package io.moonshard.moonshard.common.utils

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Base64
import android.util.DisplayMetrics
import android.view.View
import android.view.inputmethod.InputMethodManager
import java.io.ByteArrayOutputStream
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import java.util.*


object Utils {
    fun drawableToBitmap(drawable: Drawable): Bitmap {
        if (drawable is BitmapDrawable) {
            if (drawable.bitmap != null) {
                return drawable.bitmap
            }
        }
        val bitmap: Bitmap = if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
            Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        } else {
            Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
        }
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    fun bytesToBitmap(bytes: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    fun convertDpToPixel(dp: Float, context: Context?): Int {
        return if (context != null) {
            val resources = context.resources
            val metrics = resources.displayMetrics
            (dp * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).toInt()
        } else {
            val metrics = Resources.getSystem().displayMetrics
            (dp * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).toInt()
        }
    }

    fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun bitMapToString(bitmap: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val b = baos.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }

    fun stringToBitMap(encodedString: String): Bitmap? {
        try {
            val encodeByte = Base64.decode(encodedString, Base64.DEFAULT)
            return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.size)
        } catch (e: Exception) {
            e.message
            return null
        }
    }

    fun stringToByteArray(encodedString: String): ByteArray? {
        try {
            if (encodedString.isEmpty()) return null
            return Base64.decode(encodedString, Base64.DEFAULT);
        } catch (e: Exception) {
            e.message
            return null
        }
    }

    fun getNextSundayDate(): Calendar {
        val calendar = Calendar.getInstance();
        while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
            calendar.add(Calendar.DATE, 1)
        }
        return calendar
    }


     fun getNextSaturdayDate(): Calendar {
         val calendar = Calendar.getInstance();
         while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY) {
             calendar.add(Calendar.DATE, 1)
         }
         return calendar
    }
}