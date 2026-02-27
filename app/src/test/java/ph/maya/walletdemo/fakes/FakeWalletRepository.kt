package ph.maya.walletdemo.fakes

import ph.maya.walletdemo.domain.model.Transaction
import ph.maya.walletdemo.domain.model.WalletBalance
import ph.maya.walletdemo.domain.repository.WalletRepository

class FakeWalletRepository : WalletRepository {
    override suspend fun getBalance() = WalletBalance(500.0, "PHP")
    override suspend fun sendMoney(amount: Double) = Result.success(Unit)
    override suspend fun getTransactions() = emptyList<Transaction>()
}