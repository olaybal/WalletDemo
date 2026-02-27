package ph.maya.walletdemo.presentation.wallet

sealed interface WalletEvent {
    data object LoggedOut : WalletEvent
}