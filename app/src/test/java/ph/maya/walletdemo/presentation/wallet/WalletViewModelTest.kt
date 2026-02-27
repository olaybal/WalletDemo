package ph.maya.walletdemo.presentation.wallet

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test
import ph.maya.walletdemo.core.MainDispatcherRule
import ph.maya.walletdemo.domain.model.WalletBalance
import ph.maya.walletdemo.domain.repository.WalletRepository
import ph.maya.walletdemo.domain.usecase.wallet.GetWalletBalanceUseCase

@OptIn(ExperimentalCoroutinesApi::class)
class WalletViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val walletRepository = object : WalletRepository {
        override suspend fun getBalance(): WalletBalance {
            return WalletBalance(amount = 500.0, currency = "PHP")
        }
    }

    @Test
    fun `init loads balance 500 PHP`() = runTest {
        val viewModel = WalletViewModel(
            getWalletBalanceUseCase = GetWalletBalanceUseCase(walletRepository)
        )

        val state = viewModel.state.value

        assertFalse(state.isLoading)
        assertEquals(500.0, state.balance?.amount)
        assertEquals("PHP", state.balance?.currency)
        assertEquals(true, state.isBalanceVisible)
        assertEquals(null, state.errorMessage)
    }
}