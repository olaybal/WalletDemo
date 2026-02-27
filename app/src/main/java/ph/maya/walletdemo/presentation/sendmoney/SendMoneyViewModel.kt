package ph.maya.walletdemo.presentation.sendmoney

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ph.maya.walletdemo.domain.usecase.auth.LogoutUseCase
import ph.maya.walletdemo.domain.usecase.wallet.SendMoneyResult
import ph.maya.walletdemo.domain.usecase.wallet.SendMoneyUseCase
import javax.inject.Inject

@HiltViewModel
class SendMoneyViewModel @Inject constructor(
    private val sendMoneyUseCase: SendMoneyUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val stateInternal = MutableStateFlow(SendMoneyUiState())
    val state: StateFlow<SendMoneyUiState> = stateInternal.asStateFlow()

    private val eventsInternal = MutableSharedFlow<SendMoneyEvent>()
    val events: SharedFlow<SendMoneyEvent> = eventsInternal.asSharedFlow()

    fun onAmountChange(newValue: String) {
        // only keep digits and optional dot
        val filtered = newValue.filter { it.isDigit() || it == '.' }
        stateInternal.value = stateInternal.value.copy(amountText = filtered)
    }

    fun onSubmit() {
        viewModelScope.launch {
            val text = stateInternal.value.amountText.trim()
            val amount = text.toDoubleOrNull()

            if (amount == null) {
                eventsInternal.emit(SendMoneyEvent.ShowResultSheet(false, "Enter a valid amount"))
                return@launch
            }

            stateInternal.value = stateInternal.value.copy(isSubmitting = true)

            when (val result = sendMoneyUseCase(amount)) {
                SendMoneyResult.Success ->
                    eventsInternal.emit(SendMoneyEvent.ShowResultSheet(true, "Send successful"))

                is SendMoneyResult.Failure ->
                    eventsInternal.emit(SendMoneyEvent.ShowResultSheet(false, result.message))
            }

            stateInternal.value = stateInternal.value.copy(isSubmitting = false, amountText = "")
        }
    }

    fun onLogoutClick() {
        viewModelScope.launch {
            logoutUseCase()
            eventsInternal.emit(SendMoneyEvent.LoggedOut)
        }
    }
}