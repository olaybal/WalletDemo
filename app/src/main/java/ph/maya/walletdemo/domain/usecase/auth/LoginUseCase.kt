package ph.maya.walletdemo.domain.usecase.auth

import ph.maya.walletdemo.domain.repository.SessionRepository

class LoginUseCase(
    private val sessionRepository: SessionRepository
) {
    suspend operator fun invoke(
        username: String,
        password: String
    ) = sessionRepository.login(
        username = username.trim(),
        password = password
    )
}