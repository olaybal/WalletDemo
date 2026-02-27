package ph.maya.walletdemo.presentation.wallet

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeout
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import ph.maya.walletdemo.core.MainDispatcherRule
import ph.maya.walletdemo.domain.model.Session
import ph.maya.walletdemo.domain.model.Transaction
import ph.maya.walletdemo.domain.model.WalletBalance
import ph.maya.walletdemo.domain.repository.SessionRepository
import ph.maya.walletdemo.domain.repository.WalletRepository
import ph.maya.walletdemo.domain.usecase.auth.LogoutUseCase
import ph.maya.walletdemo.domain.usecase.wallet.GetWalletBalanceUseCase

@OptIn(ExperimentalCoroutinesApi::class)
class WalletViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private class FakeSessionRepository : SessionRepository {
        var didLogout = false

        override suspend fun login(
            username: String,
            password: String
        ): Result<Session> {
            return Result.failure(IllegalStateException("Not needed"))
        }

        override suspend fun logout() {
            didLogout = true
        }

        override suspend fun getSession(): Session? = null
    }

    private class FakeWalletRepository(
        private val balance: WalletBalance = WalletBalance(amount = 500.0, currency = "PHP")
    ) : WalletRepository {

        override suspend fun getBalance(): WalletBalance = balance

        override suspend fun sendMoney(amount: Double): Result<Unit> {
            return Result.failure(IllegalStateException("Not needed"))
        }

        override suspend fun getTransactions(): List<Transaction> = emptyList()
    }

    @Test
    fun `init loads balance 500 PHP and sets visible by default`() =
        runTest {
            val walletRepo = FakeWalletRepository()
            val sessionRepo = FakeSessionRepository()

            val viewModel = WalletViewModel(
                getWalletBalanceUseCase = GetWalletBalanceUseCase(walletRepo),
                logoutUseCase = LogoutUseCase(sessionRepo)
            )

            advanceUntilIdle()

            val state = viewModel.state.value
            Assert.assertFalse(state.isLoading)
            Assert.assertEquals(500.0, state.balance?.amount)
            Assert.assertEquals("PHP", state.balance?.currency)
            Assert.assertTrue(state.isBalanceVisible)
            Assert.assertEquals(null, state.errorMessage)
        }

    @Test
    fun `toggle hides and shows balance`() = runTest {
        val viewModel = WalletViewModel(
            getWalletBalanceUseCase = GetWalletBalanceUseCase(FakeWalletRepository()),
            logoutUseCase = LogoutUseCase(FakeSessionRepository())
        )

        advanceUntilIdle()

        Assert.assertTrue(viewModel.state.value.isBalanceVisible)

        viewModel.onToggleBalanceVisibility()
        Assert.assertFalse(viewModel.state.value.isBalanceVisible)

        viewModel.onToggleBalanceVisibility()
        Assert.assertTrue(viewModel.state.value.isBalanceVisible)
    }

    @Test
    fun `send money click emits NavigateToSendMoney`() = runTest {
        val viewModel = WalletViewModel(
            getWalletBalanceUseCase = GetWalletBalanceUseCase(FakeWalletRepository()),
            logoutUseCase = LogoutUseCase(FakeSessionRepository())
        )

        advanceUntilIdle()

        viewModel.onSendMoneyClick()

        val event = withTimeout(1_000) { viewModel.events.first() }
        Assert.assertEquals(WalletEvent.NavigateToSendMoney, event)
    }

    @Test
    fun `view transactions click emits NavigateToTransactions`() =
        runTest {
            val viewModel = WalletViewModel(
                getWalletBalanceUseCase = GetWalletBalanceUseCase(FakeWalletRepository()),
                logoutUseCase = LogoutUseCase(FakeSessionRepository())
            )

            advanceUntilIdle()

            viewModel.onViewTransactionsClick()

            val event = withTimeout(1_000) { viewModel.events.first() }
            Assert.assertEquals(WalletEvent.NavigateToTransactions, event)
        }

    @Test
    fun `logout emits LoggedOut and calls session logout`() =
        runTest {
            val sessionRepo = FakeSessionRepository()

            val viewModel = WalletViewModel(
                getWalletBalanceUseCase = GetWalletBalanceUseCase(FakeWalletRepository()),
                logoutUseCase = LogoutUseCase(sessionRepo)
            )

            advanceUntilIdle()

            viewModel.onLogoutClick()

            val event = withTimeout(1_000) { viewModel.events.first() }
            Assert.assertEquals(WalletEvent.LoggedOut, event)
            Assert.assertTrue(sessionRepo.didLogout)
        }
}