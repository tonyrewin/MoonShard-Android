package io.moonshard.moonshard.common

import android.content.Context
import de.adorsys.android.securestoragelibrary.SecurePreferences

private const val chunkSize = 240

private fun getNumberOfChunksKey(key: String) = "${key}_numberOfChunks"

internal fun setLongStringValue(key: String, value: String) {
    val chunks = value.chunked(chunkSize)

    SecurePreferences.setValue(getNumberOfChunksKey(key), chunks.size)

    chunks.forEachIndexed { index, chunk ->
        SecurePreferences.setValue( "$key$index", chunk)
    }
}

internal fun getLongStringValue(key: String): String? {
    val numberOfChunks = SecurePreferences.getIntValue( getNumberOfChunksKey(key), 0)

    if (numberOfChunks == 0) {
        return null
    }

    return (0 until numberOfChunks)
        .map { index ->
            val string = SecurePreferences.getStringValue( "$key$index", null) ?: run {
                return null
            }

            string
        }.reduce { accumulator, chunk -> accumulator + chunk }
}

internal fun removeLongStringValue(key: String) {
    val numberOfChunks = SecurePreferences.getIntValue(getNumberOfChunksKey(key), 0)

    (0 until numberOfChunks).map { SecurePreferences.removeValue( "$key$it") }
    SecurePreferences.removeValue(getNumberOfChunksKey(key))
}

internal fun containsLongStringValue(key: String): Boolean = SecurePreferences.contains(getNumberOfChunksKey(key))