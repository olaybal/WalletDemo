package ph.maya.walletdemo.data.repository

import ph.maya.walletdemo.domain.model.Transaction
import ph.maya.walletdemo.domain.model.WalletBalance
import ph.maya.walletdemo.domain.repository.WalletRepository
import javax.inject.Inject

class InMemoryWalletRepositoryImpl @Inject constructor() : WalletRepository {

    private var balanceAmount: Double = 500.0
    private val transactions = mutableListOf<Transaction>()

    override suspend fun getBalance(): WalletBalance {
        return WalletBalance(amount = balanceAmount, currency = "PHP")
    }

    override suspend fun sendMoney(amount: Double): Result<Unit> {
        if (amount <= 0.0) return Result.failure(IllegalArgumentException("Invalid amount"))
        if (amount > balanceAmount) return Result.failure(IllegalStateException("Insufficient balance"))

        balanceAmount -= amount
        transactions.add(Transaction(amount = amount, currency = "PHP"))
        return Result.success(Unit)
    }

    override suspend fun getTransactions(): List<Transaction> = transactions.toList()
}