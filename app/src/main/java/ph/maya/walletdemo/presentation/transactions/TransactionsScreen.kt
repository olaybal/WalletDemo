package ph.maya.walletdemo.presentation.transactions

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest
import ph.maya.walletdemo.domain.model.Transaction
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsRoute(
    onLoggedOut: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: TransactionsViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                TransactionsEvent.LoggedOut -> onLoggedOut()
            }
        }
    }

    TransactionsScreen(
        state = state,
        onLogout = viewModel::onLogoutClick,
        onNavigateBack = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    state: TransactionsUiState,
    onLogout: () -> Unit,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transactions") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = { TextButton(onClick = onLogout) { Text("Logout") } }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            when {
                state.isLoading -> Text("Loading...")
                state.errorMessage != null -> Text(state.errorMessage)
                state.items.isEmpty() -> Text("No transactions yet.")
                else -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(state.items, key = { it.id }) { tx ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val amountText = String.format(
                                    Locale.getDefault(),
                                    "%.2f",
                                    tx.amount
                                )
                                Text(text = "${tx.currency} $amountText")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TransactionsScreenPreview_Empty() {
    TransactionsScreen(
        state = TransactionsUiState(
            isLoading = false,
            items = emptyList(),
            errorMessage = null
        ),
        onLogout = {},
        onNavigateBack = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun TransactionsScreenPreview_Loading() {
    TransactionsScreen(
        state = TransactionsUiState(
            isLoading = true,
            items = emptyList(),
            errorMessage = null
        ),
        onLogout = {},
        onNavigateBack = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun TransactionsScreenPreview_Error() {
    TransactionsScreen(
        state = TransactionsUiState(
            isLoading = false,
            items = emptyList(),
            errorMessage = "Something went wrong"
        ),
        onLogout = {},
        onNavigateBack = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun TransactionsScreenPreview_List() {
    TransactionsScreen(
        state = TransactionsUiState(
            isLoading = false,
            items = listOf(
                Transaction(id = "1", amount = 100.0, currency = "PHP"),
                Transaction(id = "2", amount = 50.5, currency = "PHP"),
                Transaction(id = "3", amount = 10.0, currency = "PHP")
            ),
            errorMessage = null
        ),
        onLogout = {},
        onNavigateBack = {}
    )
}