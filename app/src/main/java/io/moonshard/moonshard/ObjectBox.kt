package io.moonshard.moonshard

import android.content.Context
import io.moonshard.moonshard.models.dbEntities.MyObjectBox
import io.objectbox.BoxStore

object ObjectBox {
    lateinit var boxStore: BoxStore
        private set

    fun init(context: Context) {
        boxStore = MyObjectBox.builder()
            .androidContext(context.applicationContext)
            .build()
    }
}