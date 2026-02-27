package ph.maya.walletdemo.presentation.wallet

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import ph.maya.walletdemo.MainDispatcherRule
import ph.maya.walletdemo.domain.model.Transaction
import ph.maya.walletdemo.domain.model.WalletBalance
import ph.maya.walletdemo.domain.repository.WalletRepository
import ph.maya.walletdemo.domain.usecase.auth.LogoutUseCase
import ph.maya.walletdemo.domain.usecase.wallet.GetWalletBalanceUseCase
import ph.maya.walletdemo.fakes.FakeSessionRepository

@OptIn(ExperimentalCoroutinesApi::class)
class WalletViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val walletRepository = object : WalletRepository {
        override suspend fun getBalance(): WalletBalance {
            return WalletBalance(amount = 500.0, currency = "PHP")
        }

        override suspend fun sendMoney(amount: Double): Result<Unit> {
            TODO("Not yet implemented")
        }

        override suspend fun getTransactions(): List<Transaction> {
            TODO("Not yet implemented")
        }
    }

    @Test
    fun `init loads balance 500 PHP`() = runTest {
        val fakeSessionRepository = FakeSessionRepository()
        val viewModel = WalletViewModel(
            getWalletBalanceUseCase = GetWalletBalanceUseCase(walletRepository),
            logoutUseCase = LogoutUseCase(fakeSessionRepository)
        )

        advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertEquals(500.0, state.balance?.amount)
        assertEquals("PHP", state.balance?.currency)
        assertEquals(true, state.isBalanceVisible)
        assertEquals(null, state.errorMessage)
    }

    @Test
    fun `toggle visibility flips isBalanceVisible`() = runTest {
        val fakeSessionRepository = FakeSessionRepository()
        val viewModel = WalletViewModel(
            getWalletBalanceUseCase = GetWalletBalanceUseCase(walletRepository),
            logoutUseCase = LogoutUseCase(fakeSessionRepository)
        )

        advanceUntilIdle()

        val before = viewModel.state.value.isBalanceVisible

        viewModel.onToggleBalanceVisibility()

        val after = viewModel.state.value.isBalanceVisible
        assertEquals(!before, after)
    }

    @Test
    fun `logout emits LoggedOut event and calls session logout`() = runTest {
        val fakeSessionRepository = FakeSessionRepository()

        val viewModel = WalletViewModel(
            getWalletBalanceUseCase = GetWalletBalanceUseCase(walletRepository),
            logoutUseCase = LogoutUseCase(fakeSessionRepository)
        )

        advanceUntilIdle()

        viewModel.onLogoutClick()

        val event = withTimeout(1_000) { viewModel.events.first() }
        assertEquals(WalletEvent.LoggedOut, event)
        assertTrue(fakeSessionRepository.didLogout)
    }

    @Test
    fun `send money click emits NavigateToSendMoney`() = runTest {
        val viewModel = WalletViewModel(
            getWalletBalanceUseCase = GetWalletBalanceUseCase(walletRepository),
            logoutUseCase = LogoutUseCase(FakeSessionRepository())
        )

        advanceUntilIdle()

        viewModel.onSendMoneyClick()

        val event = withTimeout(1_000) { viewModel.events.first() }
        assertEquals(WalletEvent.NavigateToSendMoney, event)
    }

    @Test
    fun `view transactions click emits NavigateToTransactions`() = runTest {
        val viewModel = WalletViewModel(
            getWalletBalanceUseCase = GetWalletBalanceUseCase(walletRepository),
            logoutUseCase = LogoutUseCase(FakeSessionRepository())
        )

        advanceUntilIdle()

        viewModel.onViewTransactionsClick()

        val event = withTimeout(1_000) { viewModel.events.first() }
        assertEquals(WalletEvent.NavigateToTransactions, event)
    }

}