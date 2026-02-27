package ph.maya.walletdemo.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ph.maya.walletdemo.data.repository.InMemoryWalletRepositoryImpl
import ph.maya.walletdemo.domain.repository.WalletRepository
import ph.maya.walletdemo.domain.usecase.wallet.GetWalletBalanceUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WalletModule {

    @Provides
    @Singleton
    fun provideWalletRepository(): WalletRepository = InMemoryWalletRepositoryImpl()

    @Provides
    fun provideGetWalletBalanceUseCase(
        walletRepository: WalletRepository
    ): GetWalletBalanceUseCase = GetWalletBalanceUseCase(walletRepository)
}