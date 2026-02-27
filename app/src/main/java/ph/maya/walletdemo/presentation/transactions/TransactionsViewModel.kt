package ph.maya.walletdemo.presentation.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import ph.maya.walletdemo.domain.usecase.auth.LogoutUseCase
import javax.inject.Inject

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val eventsInternal = MutableSharedFlow<TransactionsEvent>()
    val events: SharedFlow<TransactionsEvent> = eventsInternal.asSharedFlow()

    fun onLogoutClick() {
        viewModelScope.launch {
            logoutUseCase()
            eventsInternal.emit(TransactionsEvent.LoggedOut)
        }
    }
}