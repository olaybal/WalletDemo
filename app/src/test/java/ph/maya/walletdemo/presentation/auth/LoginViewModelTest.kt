package ph.maya.walletdemo.presentation.auth

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import ph.maya.walletdemo.MainDispatcherRule
import ph.maya.walletdemo.domain.model.Session
import ph.maya.walletdemo.domain.model.User
import ph.maya.walletdemo.domain.repository.SessionRepository
import ph.maya.walletdemo.domain.usecase.auth.LoginUseCase

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val sessionRepository: SessionRepository = mockk()
    private val loginUseCase = LoginUseCase(sessionRepository)

    @Test
    fun `login success sets isLoggedIn true and clears error`() = runTest {
        coEvery { sessionRepository.login("johndoe", "pass") } returns
                Result.success(Session(User("johndoe")))

        val vm = LoginViewModel(loginUseCase)

        vm.onUsernameChange("johndoe")
        vm.onPasswordChange("pass")
        vm.onLoginClick()

        advanceUntilIdle()

        assertTrue(vm.state.value.isLoggedIn)
        assertEquals(null, vm.state.value.errorMessage)

        coVerify(exactly = 1) { sessionRepository.login("johndoe", "pass") }
    }

    @Test
    fun `login failure sets errorMessage and keeps isLoggedIn false`() = runTest {
        coEvery { sessionRepository.login("johndoe", "pass") } returns
                Result.failure(IllegalArgumentException("Invalid credentials"))

        val vm = LoginViewModel(loginUseCase)

        vm.onUsernameChange("johndoe")
        vm.onPasswordChange("pass")
        vm.onLoginClick()

        advanceUntilIdle()

        assertEquals(false, vm.state.value.isLoggedIn)
        assertEquals("Invalid credentials", vm.state.value.errorMessage)
    }
}