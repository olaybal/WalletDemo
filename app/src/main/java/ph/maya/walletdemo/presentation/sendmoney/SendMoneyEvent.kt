package ph.maya.walletdemo.presentation.sendmoney

sealed interface SendMoneyEvent {
    data class ShowResultSheet(val isSuccess: Boolean, val message: String) : SendMoneyEvent
    data object LoggedOut : SendMoneyEvent
}