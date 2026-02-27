package ph.maya.walletdemo.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ph.maya.walletdemo.presentation.auth.LoginScreen
import ph.maya.walletdemo.presentation.sendmoney.SendMoneyRoute
import ph.maya.walletdemo.presentation.transactions.TransactionsRoute
import ph.maya.walletdemo.presentation.wallet.WalletRoute

@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination: String = Destinations.LOGIN
) {
    NavHost(navController = navController, startDestination = startDestination) {

        composable(Destinations.LOGIN) {
            LoginScreen(
                viewModel = hiltViewModel(it),
                onLoggedIn = {
                    navController.navigate(Destinations.WALLET) {
                        popUpTo(Destinations.LOGIN) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(route = Destinations.WALLET) {
            WalletRoute(
                onNavigateToSendMoney = { navController.navigate(Destinations.SEND_MONEY) },
                onNavigateToTransactions = { navController.navigate(Destinations.TRANSACTIONS) },
                onLoggedOut = {
                    navController.navigate(Destinations.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        composable(Destinations.SEND_MONEY) {
            SendMoneyRoute(
                viewModel = hiltViewModel(it),
                onLoggedOut = {
                    navController.navigate(Destinations.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Destinations.TRANSACTIONS) {
            TransactionsRoute(
                viewModel = hiltViewModel(it),
                onLoggedOut = {
                    navController.navigate(Destinations.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}