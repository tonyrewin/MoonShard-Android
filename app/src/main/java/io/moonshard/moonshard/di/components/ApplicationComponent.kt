package io.moonshard.moonshard.di.components

import android.app.Application
import dagger.Component
import io.moonshard.moonshard.di.modules.ApplicationModule
import io.moonshard.moonshard.di.modules.RoomModule
import io.moonshard.moonshard.di.modules.WebModule
import io.moonshard.moonshard.helpers.RoomHelper
import io.moonshard.moonshard.presentation.presenter.LoginPresenter
import io.moonshard.moonshard.repository.ChatListRepository
import io.moonshard.moonshard.repository.MessageRepository
import io.moonshard.moonshard.repository.NetworkRepository
import io.moonshard.moonshard.services.P2ChatService
import io.moonshard.moonshard.usecase.TestUseCase
import javax.inject.Singleton

@Component(modules = [ApplicationModule::class, WebModule::class, RoomModule::class])
@Singleton
interface ApplicationComponent {
    fun inject(p2chatService: P2ChatService)
    fun inject(presenter: LoginPresenter)
    fun inject(networkRepository: NetworkRepository)
    fun inject(testUseCase: TestUseCase)
    fun inject(chatListRepository: ChatListRepository)
    fun inject(messageRepository: MessageRepository)

    fun application(): Application
    fun roomHelper(): RoomHelper
    // fun inject(p2ChatModule: P2ChatModule)
}