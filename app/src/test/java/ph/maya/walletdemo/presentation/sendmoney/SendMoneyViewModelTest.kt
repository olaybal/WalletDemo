package ph.maya.walletdemo.presentation.sendmoney

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
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
import ph.maya.walletdemo.domain.usecase.wallet.SendMoneyUseCase

@OptIn(ExperimentalCoroutinesApi::class)
class SendMoneyViewModelTest {

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
        private var balance: Double = 500.0
    ) : WalletRepository {

        var lastSendAmount: Double? = null

        override suspend fun getBalance(): WalletBalance = WalletBalance(balance, "PHP")

        override suspend fun sendMoney(amount: Double): Result<Unit> {
            lastSendAmount = amount
            if (amount <= 0.0) return Result.failure(IllegalArgumentException("Invalid amount"))
            if (amount > balance) return Result.failure(IllegalStateException("Insufficient balance"))

            balance -= amount
            return Result.success(Unit)
        }

        override suspend fun getTransactions(): List<Transaction> = emptyList()
    }

    @Test
    fun `amount input keeps only digits and dot`() = runTest {
        val viewModel = SendMoneyViewModel(
            sendMoneyUseCase = SendMoneyUseCase(FakeWalletRepository()),
            logoutUseCase = LogoutUseCase(FakeSessionRepository())
        )

        viewModel.onAmountChange("abc1a0.5x0")
        assertEquals("10.50", viewModel.state.value.amountText)
    }

    @Test
    fun `submit emits failure sheet when amount is invalid`() =
        runTest {
            val viewModel = SendMoneyViewModel(
                sendMoneyUseCase = SendMoneyUseCase(FakeWalletRepository()),
                logoutUseCase = LogoutUseCase(FakeSessionRepository())
            )

            viewModel.onAmountChange("..")
            viewModel.onSubmit()

            val event = withTimeout(1_000) { viewModel.events.first() }
            assertEquals(
                SendMoneyEvent.ShowResultSheet(isSuccess = false, message = "Enter a valid amount"),
                event
            )
        }

    @Test
    fun `submit emits failure sheet when amount is greater than balance`() =
        runTest {
            val viewModel = SendMoneyViewModel(
                sendMoneyUseCase = SendMoneyUseCase(FakeWalletRepository(balance = 500.0)),
                logoutUseCase = LogoutUseCase(FakeSessionRepository())
            )

            viewModel.onAmountChange("600")
            viewModel.onSubmit()

            val event = withTimeout(1_000) { viewModel.events.first() }
            assertEquals(
                SendMoneyEvent.ShowResultSheet(isSuccess = false, message = "Insufficient balance"),
                event
            )
        }

    @Test
    fun `submit emits success sheet and calls repository when amount is within balance`() =
        runTest {
            val walletRepo = FakeWalletRepository(balance = 500.0)

            val viewModel = SendMoneyViewModel(
                sendMoneyUseCase = SendMoneyUseCase(walletRepo),
                logoutUseCase = LogoutUseCase(FakeSessionRepository())
            )

            viewModel.onAmountChange("100")
            viewModel.onSubmit()

            val event = withTimeout(1_000) { viewModel.events.first() }
            assertEquals(
                SendMoneyEvent.ShowResultSheet(isSuccess = true, message = "Send successful"),
                event
            )
            assertEquals(100.0, walletRepo.lastSendAmount)
        }

    @Test
    fun `logout emits LoggedOut and calls session logout`() =
        runTest {
            val sessionRepo = FakeSessionRepository()

            val viewModel = SendMoneyViewModel(
                sendMoneyUseCase = SendMoneyUseCase(FakeWalletRepository()),
                logoutUseCase = LogoutUseCase(sessionRepo)
            )

            viewModel.onLogoutClick()

            val event = withTimeout(1_000) { viewModel.events.first() }
            assertEquals(SendMoneyEvent.LoggedOut, event)
            assertTrue(sessionRepo.didLogout)
        }
}