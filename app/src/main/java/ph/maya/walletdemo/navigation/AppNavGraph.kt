package ph.maya.walletdemo.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ph.maya.walletdemo.presentation.auth.LoginRoute

@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination: String = Destinations.LOGIN
) {
    NavHost(navController = navController, startDestination = startDestination) {

        composable(Destinations.LOGIN) {
            LoginRoute(
                onLoggedIn = {
                    navController.navigate(Destinations.WALLET) {
                        popUpTo(Destinations.LOGIN) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}