package io.moonshard.moonshard.di.components

import dagger.Component
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.common.matrix.MatrixSdkHelper
import io.moonshard.moonshard.di.modules.ApplicationModule
import io.moonshard.moonshard.services.P2ChatService
import javax.inject.Singleton

@Component(modules = [ApplicationModule::class])
@Singleton
interface ApplicationComponent {
    fun inject(p2chatService: P2ChatService)
    fun inject(matrixSdkHelper: MatrixSdkHelper)

    // fun inject(p2ChatModule: P2ChatModule)
}