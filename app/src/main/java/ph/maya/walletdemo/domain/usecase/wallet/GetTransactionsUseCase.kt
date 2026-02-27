package ph.maya.walletdemo.domain.usecase.wallet

import ph.maya.walletdemo.domain.model.Transaction
import ph.maya.walletdemo.domain.repository.WalletRepository

class GetTransactionsUseCase(
    private val walletRepository: WalletRepository
) {
    suspend operator fun invoke(): List<Transaction> = walletRepository.getTransactions()
}