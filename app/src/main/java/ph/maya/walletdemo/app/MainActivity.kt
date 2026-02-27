package ph.maya.walletdemo.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import ph.maya.walletdemo.navigation.AppNavGraph
import ph.maya.walletdemo.presentation.core.theme.MayaWalletDemoTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MayaWalletDemoTheme {
                val navController = rememberNavController()
                AppNavGraph(navController = navController)
            }
        }
    }
}