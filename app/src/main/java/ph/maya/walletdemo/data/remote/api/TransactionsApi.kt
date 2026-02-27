package ph.maya.walletdemo.data.remote.api

import ph.maya.walletdemo.data.remote.dto.TransactionDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface TransactionsApi {

    @GET("transactions")
    suspend fun getTransactions(): List<TransactionDto>

    @POST("transactions")
    suspend fun createTransaction(@Body body: TransactionDto): TransactionDto
}