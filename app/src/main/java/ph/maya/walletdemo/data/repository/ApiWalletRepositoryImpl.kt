package ph.maya.walletdemo.data.repository

import ph.maya.walletdemo.data.mapper.toDomain
import ph.maya.walletdemo.data.remote.api.TransactionsApi
import ph.maya.walletdemo.data.remote.dto.TransactionDto
import ph.maya.walletdemo.domain.model.Transaction
import ph.maya.walletdemo.domain.model.WalletBalance
import ph.maya.walletdemo.domain.repository.WalletRepository
import javax.inject.Inject

class ApiWalletRepositoryImpl @Inject constructor(
    private val api: TransactionsApi
) : WalletRepository {

    override suspend fun getBalance(): WalletBalance {
        return WalletBalance(amount = 500.0, currency = "PHP")
    }

    override suspend fun sendMoney(amount: Double): Result<Unit> {
        return runCatching {
            api.createTransaction(
                TransactionDto(
                    amount = amount,
                    currency = "PHP"
                )
            )
            Unit
        }
    }

    override suspend fun getTransactions(): List<Transaction> {
        return api.getTransactions()
            .mapNotNull { it.toDomain() }
            // optional: show latest first (MockAPI usually returns oldest-first)
            .reversed()
    }
}