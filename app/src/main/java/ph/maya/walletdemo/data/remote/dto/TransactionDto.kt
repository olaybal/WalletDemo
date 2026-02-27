package ph.maya.walletdemo.data.remote.dto

data class TransactionDto(
    val id: String? = null,
    val amount: Double? = null,
    val currency: String? = null,
    val createdAt: String? = null
)