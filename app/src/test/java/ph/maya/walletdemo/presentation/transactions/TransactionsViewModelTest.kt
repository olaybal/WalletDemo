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
import ph.maya.walletdemo.MainDispatcherRule
import ph.maya.walletdemo.domain.model.Transaction
import ph.maya.walletdemo.domain.usecase.auth.LogoutUseCase
import ph.maya.walletdemo.domain.usecase.wallet.GetTransactionsUseCase
import ph.maya.walletdemo.fakes.FakeSessionRepository

@OptIn(ExperimentalCoroutinesApi::class)
class TransactionsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `init loads transactions list`() = runTest {
        val fakeRepo = object : ph.maya.walletdemo.domain.repository.WalletRepository {
            override suspend fun getBalance() = throw IllegalStateException("Not needed")
            override suspend fun sendMoney(amount: Double) =
                throw IllegalStateException("Not needed")

            override suspend fun getTransactions(): List<Transaction> = listOf(
                Transaction(id = "1", amount = 100.0, currency = "PHP"),
                Transaction(id = "2", amount = 50.0, currency = "PHP")
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
        assertEquals(100.0, state.items[0].amount)
        assertEquals(null, state.errorMessage)
    }

    @Test
    fun `init sets error when loading transactions fails`() = runTest {
        val fakeRepo = object : ph.maya.walletdemo.domain.repository.WalletRepository {
            override suspend fun getBalance() = throw IllegalStateException("Not needed")
            override suspend fun sendMoney(amount: Double) =
                throw IllegalStateException("Not needed")

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
    fun `logout emits LoggedOut and calls session logout`() = runTest {
        val fakeRepo = object : ph.maya.walletdemo.domain.repository.WalletRepository {
            override suspend fun getBalance() = throw IllegalStateException("Not needed")
            override suspend fun sendMoney(amount: Double) =
                throw IllegalStateException("Not needed")

            override suspend fun getTransactions(): List<Transaction> = emptyList()
        }

        val fakeSessionRepository = FakeSessionRepository()

        val viewModel = TransactionsViewModel(
            getTransactionsUseCase = GetTransactionsUseCase(fakeRepo),
            logoutUseCase = LogoutUseCase(fakeSessionRepository)
        )

        viewModel.onLogoutClick()

        val event = withTimeout(1_000) { viewModel.events.first() }
        assertEquals(TransactionsEvent.LoggedOut, event)
        assertTrue(fakeSessionRepository.didLogout)
    }
}