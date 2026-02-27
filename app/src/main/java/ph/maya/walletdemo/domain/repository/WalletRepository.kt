package ph.maya.walletdemo.domain.repository

import ph.maya.walletdemo.domain.model.WalletBalance

interface WalletRepository {
    suspend fun getBalance(): WalletBalance
}