package ph.maya.walletdemo.presentation.wallet

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest
import ph.maya.walletdemo.domain.model.WalletBalance
import java.util.Locale

@Composable
fun WalletRoute(
    onNavigateToSendMoney: () -> Unit,
    onNavigateToTransactions: () -> Unit,
    onLoggedOut: () -> Unit,
    viewModel: WalletViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                WalletEvent.NavigateToSendMoney -> onNavigateToSendMoney()
                WalletEvent.NavigateToTransactions -> onNavigateToTransactions()
                WalletEvent.LoggedOut -> onLoggedOut()
            }
        }
    }

    WalletScreen(
        state = state,
        onToggleVisibility = viewModel::onToggleBalanceVisibility,
        onSendMoney = viewModel::onSendMoneyClick,
        onViewTransactions = viewModel::onViewTransactionsClick,
        onLogout = viewModel::onLogoutClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletScreen(
    state: WalletUiState,
    onToggleVisibility: () -> Unit,
    onSendMoney: () -> Unit,
    onViewTransactions: () -> Unit,
    onLogout: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Wallet") },
                actions = {
                    TextButton(onClick = onLogout) {
                        Text("Logout")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors()
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {

            Spacer(modifier = Modifier.height(24.dp))

            when {
                state.isLoading -> CircularProgressIndicator()

                state.errorMessage != null -> Text(text = state.errorMessage)

                else -> {
                    val formattedBalance = remember(state.balance) {
                        val amount = state.balance?.amount ?: 0.0
                        String.format(Locale.getDefault(), "%.2f", amount)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {

                        Text(
                            text = (state.balance?.currency ?: "PHP") + if (state.isBalanceVisible)
                                " $formattedBalance"
                            else " ******",
                            style = MaterialTheme.typography.headlineMedium
                        )

                        IconButton(onClick = onToggleVisibility) {
                            Icon(
                                imageVector = if (state.isBalanceVisible)
                                    Icons.Filled.Visibility
                                else
                                    Icons.Filled.VisibilityOff,
                                contentDescription = "Toggle balance visibility"
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = onSendMoney,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Send Money")
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = onViewTransactions,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("View Transactions")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun WalletScreenPreview_LoadedVisible() {
    WalletScreen(
        state = WalletUiState(
            isLoading = false,
            balance = WalletBalance(amount = 500.0, currency = "PHP"),
            isBalanceVisible = true
        ),
        onToggleVisibility = {},
        onSendMoney = {},
        onViewTransactions = {},
        onLogout = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun WalletScreenPreview_LoadedHidden() {
    WalletScreen(
        state = WalletUiState(
            isLoading = false,
            balance = WalletBalance(amount = 500.0, currency = "PHP"),
            isBalanceVisible = false
        ),
        onToggleVisibility = {},
        onSendMoney = {},
        onViewTransactions = {},
        onLogout = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun WalletScreenPreview_Loading() {
    WalletScreen(
        state = WalletUiState(isLoading = true),
        onToggleVisibility = {},
        onSendMoney = {},
        onViewTransactions = {},
        onLogout = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun WalletScreenPreview_Error() {
    WalletScreen(
        state = WalletUiState(
            isLoading = false,
            errorMessage = "Something went wrong"
        ),
        onToggleVisibility = {},
        onSendMoney = {},
        onViewTransactions = {},
        onLogout = {}
    )
}