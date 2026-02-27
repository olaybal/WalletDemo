package ph.maya.walletdemo.presentation.sendmoney

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
class SendMoneyViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val eventsInternal = MutableSharedFlow<SendMoneyEvent>()
    val events: SharedFlow<SendMoneyEvent> = eventsInternal.asSharedFlow()

    fun onLogoutClick() {
        viewModelScope.launch {
            logoutUseCase()
            eventsInternal.emit(SendMoneyEvent.LoggedOut)
        }
    }
}