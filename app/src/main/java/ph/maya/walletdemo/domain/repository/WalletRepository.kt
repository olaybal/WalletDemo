package ph.maya.walletdemo.domain.repository

import ph.maya.walletdemo.domain.model.Transaction
import ph.maya.walletdemo.domain.model.WalletBalance

interface WalletRepository {
    suspend fun getBalance(): WalletBalance
    suspend fun sendMoney(amount: Double): Result<Unit>
    suspend fun getTransactions(): List<Transaction>
}