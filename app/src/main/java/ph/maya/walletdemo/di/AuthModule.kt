package ph.maya.walletdemo.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import ph.maya.walletdemo.data.repository.InMemorySessionRepositoryImpl
import ph.maya.walletdemo.domain.repository.SessionRepository
import ph.maya.walletdemo.domain.usecase.auth.LoginUseCase
import ph.maya.walletdemo.domain.usecase.auth.LogoutUseCase

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    fun provideSessionRepository(): SessionRepository = InMemorySessionRepositoryImpl()

    @Provides
    fun provideLoginUseCase(repo: SessionRepository): LoginUseCase = LoginUseCase(repo)

    @Provides
    fun provideLogoutUseCase(repo: SessionRepository): LogoutUseCase = LogoutUseCase(repo)
}