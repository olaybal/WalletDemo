package ph.maya.walletdemo.data.repository

import ph.maya.walletdemo.domain.model.Session
import ph.maya.walletdemo.domain.model.User
import ph.maya.walletdemo.domain.repository.SessionRepository

class InMemorySessionRepositoryImpl : SessionRepository {

    private var session: Session? = null

    override suspend fun login(username: String, password: String): Result<Session> {
        val trimmedUsername = username.trim()

        if (trimmedUsername.isBlank()) {
            return Result.failure(IllegalArgumentException("Username is required"))
        }

        if (password.isBlank()) {
            return Result.failure(IllegalArgumentException("Password is required"))
        }

        val newSession = Session(user = User(username = trimmedUsername))
        session = newSession
        return Result.success(newSession)
    }

    override suspend fun logout() {
        session = null
    }

    override suspend fun getSession(): Session? = session
}