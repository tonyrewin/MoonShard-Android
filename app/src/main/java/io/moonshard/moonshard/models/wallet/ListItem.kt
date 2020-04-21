package io.moonshard.moonshard.models.wallet

abstract class ListItem {
    abstract val type: Int

    companion object {
        const val TYPE_DATE = 0
        const val TYPE_GENERAL = 1
    }
}