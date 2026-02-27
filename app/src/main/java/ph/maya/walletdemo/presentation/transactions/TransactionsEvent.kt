package ph.maya.walletdemo.presentation.transactions

sealed interface TransactionsEvent {
    data object LoggedOut : TransactionsEvent
}