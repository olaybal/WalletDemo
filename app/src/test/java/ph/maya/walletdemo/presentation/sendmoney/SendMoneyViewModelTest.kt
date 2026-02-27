package ph.maya.walletdemo.presentation.sendmoney

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import ph.maya.walletdemo.MainDispatcherRule
import ph.maya.walletdemo.domain.model.Transaction
import ph.maya.walletdemo.domain.model.WalletBalance
import ph.maya.walletdemo.domain.repository.WalletRepository
import ph.maya.walletdemo.domain.usecase.auth.LogoutUseCase
import ph.maya.walletdemo.domain.usecase.wallet.SendMoneyUseCase
import ph.maya.walletdemo.fakes.FakeSessionRepository

@OptIn(ExperimentalCoroutinesApi::class)
class SendMoneyViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `logout emits LoggedOut and calls session logout`() = runTest {
        val fakeSessionRepository = FakeSessionRepository()
        val viewModel = SendMoneyViewModel(
            logoutUseCase = LogoutUseCase(fakeSessionRepository)
        )

        viewModel.onLogoutClick()

        val event = withTimeout(1_000) { viewModel.events.first() }
        assertEquals(SendMoneyEvent.LoggedOut, event)
        assertTrue(fakeSessionRepository.didLogout)
    }

    @Test
    fun `submit fails when amount is greater than balance`() = runTest {
        val walletRepo = object : WalletRepository {
            override suspend fun getBalance() = WalletBalance(500.0, "PHP")
            override suspend fun sendMoney(amount: Double) = Result.success(Unit)
            override suspend fun getTransactions() = emptyList<Transaction>()
        }

        val viewModel = SendMoneyViewModel(
            sendMoneyUseCase = SendMoneyUseCase(walletRepo),
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
}