package ph.maya.walletdemo.fakes

import ph.maya.walletdemo.domain.model.Session
import ph.maya.walletdemo.domain.repository.SessionRepository

class FakeSessionRepository : SessionRepository {

    var didLogout: Boolean = false

    override suspend fun login(username: String, password: String): Result<Session> {
        return Result.failure(IllegalStateException("Not needed in this test"))
    }

    override suspend fun logout() {
        didLogout = true
    }

    override suspend fun getSession(): Session? = null
}