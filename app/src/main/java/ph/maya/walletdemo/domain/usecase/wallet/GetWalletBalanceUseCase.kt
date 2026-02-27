package ph.maya.walletdemo.domain.usecase.wallet

import ph.maya.walletdemo.domain.model.WalletBalance
import ph.maya.walletdemo.domain.repository.WalletRepository

class GetWalletBalanceUseCase(
    private val walletRepository: WalletRepository
) {
    suspend operator fun invoke(): WalletBalance = walletRepository.getBalance()
}