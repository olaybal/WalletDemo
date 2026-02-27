package ph.maya.walletdemo.presentation.transactions

import ph.maya.walletdemo.domain.model.Transaction

data class TransactionsUiState(
    val isLoading: Boolean = false,
    val items: List<Transaction> = emptyList(),
    val errorMessage: String? = null
)