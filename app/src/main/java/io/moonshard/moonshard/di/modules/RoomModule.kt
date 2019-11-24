package io.moonshard.moonshard.di.modules

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import io.moonshard.moonshard.helpers.RoomHelper
import io.moonshard.moonshard.models.dao.ChatDao
import io.moonshard.moonshard.models.dao.MessageDao
import javax.inject.Singleton


@Module
class RoomModule(mApplication: Application?) {
    private val roomHelper: RoomHelper =
        Room.databaseBuilder(mApplication!!.applicationContext, RoomHelper::class.java, "chatDB")
            .fallbackToDestructiveMigration() // FIXME   ONLY FOR TEST ENVIRONMENT! DON'T USE THIS IN PRODUCTION!
            .allowMainThreadQueries()
            .build()

    @Singleton
    @Provides
    fun providesRoomDatabase(): RoomHelper {
        return roomHelper
    }

    @Singleton
    @Provides
    fun providesChatDao(roomHelper: RoomHelper): ChatDao {
        return roomHelper.chatDao()
    }

    @Singleton
    @Provides
    fun providesMessageDao(roomHelper: RoomHelper): MessageDao {
        return roomHelper.messageDao()
    }
}