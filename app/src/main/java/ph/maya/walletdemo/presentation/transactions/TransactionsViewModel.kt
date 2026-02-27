package ph.maya.walletdemo.presentation.transactions

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
import ph.maya.walletdemo.domain.usecase.wallet.GetTransactionsUseCase
import javax.inject.Inject

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val stateInternal = MutableStateFlow(
        TransactionsUiState(
            isLoading = true,
            items = emptyList(),
            errorMessage = null
        )
    )
    val state: StateFlow<TransactionsUiState> = stateInternal.asStateFlow()

    private val eventsInternal = MutableSharedFlow<TransactionsEvent>()
    val events: SharedFlow<TransactionsEvent> = eventsInternal.asSharedFlow()

    init {
        loadTransactions()
    }

    fun onLogoutClick() {
        viewModelScope.launch {
            logoutUseCase()
            eventsInternal.emit(TransactionsEvent.LoggedOut)
        }
    }

    private fun loadTransactions() {
        viewModelScope.launch {
            stateInternal.value = stateInternal.value.copy(
                isLoading = true,
                errorMessage = null
            )

            runCatching { getTransactionsUseCase() }
                .onSuccess { list ->
                    stateInternal.value = stateInternal.value.copy(
                        isLoading = false,
                        items = list,
                        errorMessage = null
                    )
                }
                .onFailure { throwable ->
                    stateInternal.value = stateInternal.value.copy(
                        isLoading = false,
                        errorMessage = throwable.message ?: "Failed to load transactions"
                    )
                }
        }
    }
}