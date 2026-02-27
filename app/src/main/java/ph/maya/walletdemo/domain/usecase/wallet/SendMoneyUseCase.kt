package ph.maya.walletdemo.domain.usecase.wallet

import ph.maya.walletdemo.domain.repository.WalletRepository

sealed interface SendMoneyResult {
    data object Success : SendMoneyResult
    data class Failure(val message: String) : SendMoneyResult
}

class SendMoneyUseCase(
    private val walletRepository: WalletRepository
) {
    suspend operator fun invoke(amount: Double): SendMoneyResult {
        if (amount <= 0.0) return SendMoneyResult.Failure("Enter a valid amount")

        val balance = walletRepository.getBalance().amount
        if (amount > balance) return SendMoneyResult.Failure("Insufficient balance")

        return walletRepository.sendMoney(amount)
            .fold(
                onSuccess = { SendMoneyResult.Success },
                onFailure = { SendMoneyResult.Failure(it.message ?: "Send failed") }
            )
    }
}