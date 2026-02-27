package ph.maya.walletdemo.presentation.sendmoney

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SendMoneyRoute(
    onLoggedOut: () -> Unit
) {
    SendMoneyScreen(onLogout = onLoggedOut)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SendMoneyScreen(
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
            Text("Send Money screen (placeholder)")
        }
    }
}