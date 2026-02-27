package ph.maya.walletdemo.presentation.sendmoney

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SendMoneyRoute(
    onLoggedOut: () -> Unit,
    viewModel: SendMoneyViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    var sheetData by remember { mutableStateOf<SendMoneyEvent.ShowResultSheet?>(null) }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is SendMoneyEvent.ShowResultSheet -> sheetData = event
                SendMoneyEvent.LoggedOut -> onLoggedOut()
            }
        }
    }

    SendMoneyScreen(
        state = state,
        sheetData = sheetData,
        onDismissSheet = { sheetData = null },
        onAmountChange = viewModel::onAmountChange,
        onSubmit = viewModel::onSubmit,
        onLogout = viewModel::onLogoutClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SendMoneyScreen(
    state: SendMoneyUiState,
    sheetData: SendMoneyEvent.ShowResultSheet?,
    onDismissSheet: () -> Unit,
    onAmountChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onLogout: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Send Money") },
                actions = {
                    TextButton(onClick = onLogout) { Text("Logout") }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Enter amount")

            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = state.amountText,
                onValueChange = onAmountChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("0.00") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onSubmit,
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isSubmitting
            ) {
                Text(if (state.isSubmitting) "Submitting..." else "Submit")
            }
        }
    }

    if (sheetData != null) {
        ModalBottomSheet(onDismissRequest = onDismissSheet) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (sheetData.isSuccess) "Success" else "Failed"
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = sheetData.message)

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onDismissSheet,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("OK")
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SendMoneyScreenPreview_Default() {
    SendMoneyScreen(
        state = SendMoneyUiState(
            amountText = "100",
            isSubmitting = false
        ),
        sheetData = null,
        onDismissSheet = {},
        onAmountChange = {},
        onSubmit = {},
        onLogout = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun SendMoneyScreenPreview_Submitting() {
    SendMoneyScreen(
        state = SendMoneyUiState(
            amountText = "250",
            isSubmitting = true
        ),
        sheetData = null,
        onDismissSheet = {},
        onAmountChange = {},
        onSubmit = {},
        onLogout = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun SendMoneyScreenPreview_SuccessSheet() {
    SendMoneyScreen(
        state = SendMoneyUiState(
            amountText = "50",
            isSubmitting = false
        ),
        sheetData = SendMoneyEvent.ShowResultSheet(
            isSuccess = true,
            message = "Send successful"
        ),
        onDismissSheet = {},
        onAmountChange = {},
        onSubmit = {},
        onLogout = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun SendMoneyScreenPreview_FailureSheet() {
    SendMoneyScreen(
        state = SendMoneyUiState(
            amountText = "600",
            isSubmitting = false
        ),
        sheetData = SendMoneyEvent.ShowResultSheet(
            isSuccess = false,
            message = "Insufficient balance"
        ),
        onDismissSheet = {},
        onAmountChange = {},
        onSubmit = {},
        onLogout = {}
    )
}