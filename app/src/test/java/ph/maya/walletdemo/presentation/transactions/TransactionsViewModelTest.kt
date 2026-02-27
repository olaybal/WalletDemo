package ph.maya.walletdemo.presentation.transactions

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import ph.maya.walletdemo.MainDispatcherRule
import ph.maya.walletdemo.domain.usecase.auth.LogoutUseCase
import ph.maya.walletdemo.fakes.FakeSessionRepository

@OptIn(ExperimentalCoroutinesApi::class)
class TransactionsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `logout emits LoggedOut and calls session logout`() = runTest {
        val fakeSessionRepository = FakeSessionRepository()
        val viewModel = TransactionsViewModel(
            logoutUseCase = LogoutUseCase(fakeSessionRepository)
        )

        viewModel.onLogoutClick()

        val event = withTimeout(1_000) { viewModel.events.first() }
        assertEquals(TransactionsEvent.LoggedOut, event)
        assertTrue(fakeSessionRepository.didLogout)
    }
}