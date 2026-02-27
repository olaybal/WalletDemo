package ph.maya.walletdemo.data.mapper

import ph.maya.walletdemo.data.remote.dto.TransactionDto
import ph.maya.walletdemo.domain.model.Transaction

fun TransactionDto.toDomain(): Transaction? {
    val safeAmount = amount ?: return null
    val safeCurrency = currency ?: "PHP"
    return Transaction(
        id = id ?: "",
        amount = safeAmount,
        currency = safeCurrency,
        createdAt = createdAt
    )
}