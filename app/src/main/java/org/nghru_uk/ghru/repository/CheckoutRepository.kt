package org.nghru_uk.ghru.repository

import androidx.lifecycle.LiveData
import org.nghru_uk.ghru.AppExecutors
import org.nghru_uk.ghru.api.ApiResponse
import org.nghru_uk.ghru.api.NghruService
import org.nghru_uk.ghru.vo.*
import org.nghru_uk.ghru.vo.request.CheckoutRequest
import org.nghru_uk.ghru.vo.request.CheckoutRequestUK
import java.io.Serializable
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Repository that handles User objects.
 */

@Singleton
class CheckoutRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    private val nghruService: NghruService
) : Serializable {

    fun syncChk(
        chkRequest: CheckoutRequest,
        screening_id: String

    ): LiveData<Resource<ResourceData<Message>>> {
        return object : NetworkOnlyBcakgroundBoundResource<ResourceData<Message>>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<ResourceData<Message>>> {
                return nghruService.addCheckout(screening_id, chkRequest)
            }
        }.asLiveData()
    }

    fun syncChkUK(
        chkRequest: CheckoutRequestUK,
        screening_id: String

    ): LiveData<Resource<ResourceData<Message>>> {
        return object : NetworkOnlyBcakgroundBoundResource<ResourceData<Message>>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<ResourceData<Message>>> {
                return nghruService.addCheckoutUK(screening_id, chkRequest)
            }
        }.asLiveData()
    }
}
