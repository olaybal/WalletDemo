package ph.maya.walletdemo.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ph.maya.walletdemo.data.remote.api.TransactionsApi
import ph.maya.walletdemo.data.repository.ApiWalletRepositoryImpl
import ph.maya.walletdemo.domain.repository.WalletRepository
import ph.maya.walletdemo.domain.usecase.wallet.GetTransactionsUseCase
import ph.maya.walletdemo.domain.usecase.wallet.GetWalletBalanceUseCase
import ph.maya.walletdemo.domain.usecase.wallet.SendMoneyUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WalletModule {

    @Provides
    @Singleton
    fun provideWalletRepository(api: TransactionsApi): WalletRepository =
        ApiWalletRepositoryImpl(api)

    @Provides
    fun provideGetWalletBalanceUseCase(
        walletRepository: WalletRepository
    ): GetWalletBalanceUseCase = GetWalletBalanceUseCase(walletRepository)

    @Provides
    fun provideSendMoneyUseCase(walletRepository: WalletRepository): SendMoneyUseCase =
        SendMoneyUseCase(walletRepository)

    @Provides
    fun provideGetTransactionsUseCase(walletRepository: WalletRepository): GetTransactionsUseCase =
        GetTransactionsUseCase(walletRepository)
}