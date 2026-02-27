package ph.maya.walletdemo.domain.usecase.auth

import ph.maya.walletdemo.domain.repository.SessionRepository

class LogoutUseCase(
    private val sessionRepository: SessionRepository
) {
    suspend operator fun invoke() {
        sessionRepository.logout()
    }
}