package ph.maya.walletdemo.data.repository

import ph.maya.walletdemo.domain.model.WalletBalance
import ph.maya.walletdemo.domain.repository.WalletRepository
import javax.inject.Inject

class InMemoryWalletRepositoryImpl @Inject constructor() : WalletRepository {
    override suspend fun getBalance(): WalletBalance {
        return WalletBalance(amount = 500.0, currency = "PHP")
    }
}