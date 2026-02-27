package ph.maya.walletdemo.presentation.wallet

import ph.maya.walletdemo.domain.model.WalletBalance

data class WalletUiState(
    val isLoading: Boolean = false,
    val balance: WalletBalance? = null,
    val isBalanceVisible: Boolean = true,
    val errorMessage: String? = null
)