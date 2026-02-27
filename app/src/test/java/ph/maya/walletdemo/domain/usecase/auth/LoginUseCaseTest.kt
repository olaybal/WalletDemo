package ph.maya.walletdemo.domain.usecase.auth

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Test
import ph.maya.walletdemo.domain.model.Session
import ph.maya.walletdemo.domain.model.User
import ph.maya.walletdemo.domain.repository.SessionRepository

class LoginUseCaseTest {

    private val sessionRepository: SessionRepository = mockk()
    private val loginUseCase = LoginUseCase(sessionRepository)

    @Test
    fun `invokes repository login with trimmed username`() = runTest {
        val session = Session(User("johndoe"))
        coEvery { sessionRepository.login("johndoe", "pass") } returns Result.success(session)

        val result = loginUseCase("  johndoe  ", "pass")

        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { sessionRepository.login("johndoe", "pass") }
    }
}