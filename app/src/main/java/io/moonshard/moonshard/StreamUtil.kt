package io.moonshard.moonshard

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import io.reactivex.Single

import org.apache.commons.io.IOUtils

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.text.DecimalFormat

object StreamUtil {

    val PREFIX = "stream2file"
    val SUFFIX = ".tmp"

    @Throws(IOException::class)
    fun stream2file(inputStream: InputStream): File {
        val tempFile = File.createTempFile(PREFIX, SUFFIX)
        tempFile.deleteOnExit()
        FileOutputStream(tempFile).use { out -> IOUtils.copy(inputStream, out) }
        return tempFile
    }

    @Throws(IOException::class)
    fun stream2file(context: Context, uri: Uri, inputStream: InputStream): File {

        val fileNameWithFormat = getFileName(context, uri)
        val fileName =
            fileNameWithFormat.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0] // "\\. equals "." "
        val formatFile =
            "." + fileNameWithFormat.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]

        val tempFile = File.createTempFile(fileName, formatFile)
        tempFile.deleteOnExit()
        FileOutputStream(tempFile).use { out -> IOUtils.copy(inputStream, out) }
        return tempFile
    }

    fun getFileName(context: Context, uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                cursor!!.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result.substring(cut + 1)
            }
        }
        return result
    }

    fun getSizeFile(path:String):Single<String>{
        return Single.create {
            try {
                val url = URL(path)
                val urlConnection = url.openConnection()
                urlConnection.connect()
                val size = convertSizeToCorrectValue(urlConnection.contentLength)
                 it.onSuccess(size)
            } catch (e: Exception) {
                it.onError(Throwable("error"))
            }

        }
    }

    fun convertSizeToCorrectValue(size: Int): String {
        val hrSize: String
        val kb = (size / 1024) //this kb
        val dec = DecimalFormat("0.00")

        hrSize = if (kb >=1024) {
            dec.format(kb/1024) + (" MB")
        } else {
            "$kb KB"
        }
        return hrSize
    }
}
