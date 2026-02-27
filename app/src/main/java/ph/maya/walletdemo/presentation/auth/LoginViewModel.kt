package ph.maya.walletdemo.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ph.maya.walletdemo.domain.usecase.auth.LoginUseCase
import javax.inject.Inject

@HiltViewModel
class LoginViewModel  @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val mutableState = MutableStateFlow(LoginUiState())
    val state: StateFlow<LoginUiState> = mutableState

    fun onUsernameChange(value: String) {
        mutableState.update { it.copy(username = value, errorMessage = null) }
    }

    fun onPasswordChange(value: String) {
        mutableState.update { it.copy(password = value, errorMessage = null) }
    }

    fun onLoginClick() {
        val username = state.value.username
        val password = state.value.password

        mutableState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val result = loginUseCase(username, password)

            result
                .onSuccess {
                    mutableState.update {
                        it.copy(
                            isLoading = false,
                            isLoggedIn = true,
                            errorMessage = null
                        )
                    }
                }
                .onFailure { e ->
                    mutableState.update {
                        it.copy(
                            isLoading = false,
                            isLoggedIn = false,
                            errorMessage = e.message ?: "Login failed"
                        )
                    }
                }
        }
    }
}