package io.moonshard.moonshard.di.modules

import dagger.Module
import dagger.Provides
import io.moonshard.moonshard.repository.ChatListRepository
import io.moonshard.moonshard.repository.MessageRepository
import javax.inject.Singleton

@Module
class RepositoriesModule {
    @Provides
    @Singleton
    fun provideChatListRepository(): ChatListRepository {
        return ChatListRepository()
    }

    @Provides
    @Singleton
    fun provideMessageRepository(): MessageRepository {
        return MessageRepository()
    }
}