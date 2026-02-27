package ph.maya.walletdemo.presentation.wallet

sealed interface WalletEvent {
    data object LoggedOut : WalletEvent
    data object NavigateToSendMoney : WalletEvent
    data object NavigateToTransactions : WalletEvent
}