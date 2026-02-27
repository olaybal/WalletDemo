package ph.maya.walletdemo.presentation.transactions

import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import ph.maya.walletdemo.core.MainDispatcherRule
import ph.maya.walletdemo.domain.model.Session
import ph.maya.walletdemo.domain.model.Transaction
import ph.maya.walletdemo.domain.model.WalletBalance
import ph.maya.walletdemo.domain.repository.SessionRepository
import ph.maya.walletdemo.domain.repository.WalletRepository
import ph.maya.walletdemo.domain.usecase.auth.LogoutUseCase
import ph.maya.walletdemo.domain.usecase.wallet.GetTransactionsUseCase

@OptIn(ExperimentalCoroutinesApi::class)
class TransactionsViewModelTest {

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

    @Test
    fun `init loads transactions list`() = runTest {
        val fakeRepo = object : WalletRepository {
            override suspend fun getBalance(): WalletBalance = WalletBalance(500.0, "PHP")
            override suspend fun sendMoney(amount: Double): Result<Unit> = Result.success(Unit)
            override suspend fun getTransactions(): List<Transaction> = listOf(
                Transaction(amount = 100.0, currency = "PHP"),
                Transaction(amount = 50.0, currency = "PHP")
            )
        }

        val viewModel = TransactionsViewModel(
            getTransactionsUseCase = GetTransactionsUseCase(fakeRepo),
            logoutUseCase = LogoutUseCase(FakeSessionRepository())
        )

        advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertEquals(2, state.items.size)
        assertEquals(100.0, state.items.first().amount)
        assertEquals(null, state.errorMessage)
    }

    @Test
    fun `init sets error when loading transactions fails`() =
        runTest {
            val fakeRepo = object : WalletRepository {
                override suspend fun getBalance(): WalletBalance = WalletBalance(500.0, "PHP")
                override suspend fun sendMoney(amount: Double): Result<Unit> = Result.success(Unit)
                override suspend fun getTransactions(): List<Transaction> {
                    throw RuntimeException("Boom")
                }
            }

            val viewModel = TransactionsViewModel(
                getTransactionsUseCase = GetTransactionsUseCase(fakeRepo),
                logoutUseCase = LogoutUseCase(FakeSessionRepository())
            )

            advanceUntilIdle()

            val state = viewModel.state.value
            assertFalse(state.isLoading)
            assertEquals(0, state.items.size)
            assertEquals("Boom", state.errorMessage)
        }

    @Test
    fun `logout emits LoggedOut and calls session logout`() =
        runTest {
            val fakeRepo = object : WalletRepository {
                override suspend fun getBalance(): WalletBalance = WalletBalance(500.0, "PHP")
                override suspend fun sendMoney(amount: Double): Result<Unit> = Result.success(Unit)
                override suspend fun getTransactions(): List<Transaction> = emptyList()
            }

            val sessionRepo = FakeSessionRepository()

            val viewModel = TransactionsViewModel(
                getTransactionsUseCase = GetTransactionsUseCase(fakeRepo),
                logoutUseCase = LogoutUseCase(sessionRepo)
            )

            viewModel.onLogoutClick()

            val event = withTimeout(1_000) { viewModel.events.first() }
            assertEquals(TransactionsEvent.LoggedOut, event)
            assertTrue(sessionRepo.didLogout)
        }
}