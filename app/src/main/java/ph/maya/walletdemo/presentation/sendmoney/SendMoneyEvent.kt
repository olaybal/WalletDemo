package ph.maya.walletdemo.presentation.sendmoney

sealed interface SendMoneyEvent {
    data object LoggedOut : SendMoneyEvent
}