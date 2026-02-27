package ph.maya.walletdemo.domain.repository

import ph.maya.walletdemo.domain.model.Session

interface SessionRepository {
    suspend fun login(username: String, password: String): Result<Session>
    suspend fun logout()
    suspend fun getSession(): Session?
}