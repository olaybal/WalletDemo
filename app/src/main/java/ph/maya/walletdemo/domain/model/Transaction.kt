package ph.maya.walletdemo.domain.model

import java.util.UUID

data class Transaction(
    val id: String = UUID.randomUUID().toString(),
    val amount: Double,
    val currency: String = "PHP",
    val createdAt: Long?
)