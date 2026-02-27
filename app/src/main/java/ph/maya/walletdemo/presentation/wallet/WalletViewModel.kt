package ph.maya.walletdemo.presentation.wallet

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
import ph.maya.walletdemo.domain.usecase.wallet.GetWalletBalanceUseCase
import javax.inject.Inject

@HiltViewModel
class WalletViewModel @Inject constructor(
    private val getWalletBalanceUseCase: GetWalletBalanceUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val stateInternal = MutableStateFlow(
        WalletUiState(
            isLoading = true,
            isBalanceVisible = true
        )
    )
    val state: StateFlow<WalletUiState> = stateInternal.asStateFlow()

    private val eventsInternal = MutableSharedFlow<WalletEvent>()
    val events: SharedFlow<WalletEvent> = eventsInternal.asSharedFlow()

    init {
        viewModelScope.launch {
            runCatching { getWalletBalanceUseCase() }
                .onSuccess { balance ->
                    stateInternal.value = stateInternal.value.copy(
                        isLoading = false,
                        balance = balance,
                        errorMessage = null
                    )
                }
                .onFailure { throwable ->
                    stateInternal.value = stateInternal.value.copy(
                        isLoading = false,
                        errorMessage = throwable.message ?: "Something went wrong"
                    )
                }
        }
    }

    fun onToggleBalanceVisibility() {
        val current = stateInternal.value
        stateInternal.value = current.copy(isBalanceVisible = !current.isBalanceVisible)
    }

    fun onLogoutClick() {
        viewModelScope.launch {
            logoutUseCase()
            eventsInternal.emit(WalletEvent.LoggedOut)
        }
    }

    fun onSendMoneyClick() {
        viewModelScope.launch {
            eventsInternal.emit(WalletEvent.NavigateToSendMoney)
        }
    }

    fun onViewTransactionsClick() {
        viewModelScope.launch {
            eventsInternal.emit(WalletEvent.NavigateToTransactions)
        }
    }
}