package ph.maya.walletdemo.presentation.sendmoney

data class SendMoneyUiState(
    val amountText: String = "",
    val isSubmitting: Boolean = false
)