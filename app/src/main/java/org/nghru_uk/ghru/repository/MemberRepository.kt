package org.nghru_uk.ghru.repository

import androidx.lifecycle.LiveData
import org.nghru_uk.ghru.AppExecutors
import org.nghru_uk.ghru.api.ApiResponse
import org.nghru_uk.ghru.api.NghruService
import org.nghru_uk.ghru.vo.Resource
import org.nghru_uk.ghru.vo.ResourceData
import org.nghru_uk.ghru.vo.request.Member
import java.io.Serializable
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Repository that handles User objects.
 */

@Singleton
class MemberRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    private val nghruService: NghruService
) : Serializable {


    fun getMember(
        householdId: String
    ): LiveData<Resource<ResourceData<List<Member>>>> {
        return object : NetworkOnlyBoundResource<ResourceData<List<Member>>>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<ResourceData<List<Member>>>> {
                return nghruService.getMember(householdId, "0")
            }
        }.asLiveData()
    }


//    fun getMemberLocal(householdId: String
//    ): LiveData<Resource<ResourceData<List<Member>>>> {
//        return object : NetworkOnlyBoundResource<MemberData, ResourceData<List<Member>>>(appExecutors) {
//            override fun createCall(): LiveData<ApiResponse<ResourceData<List<Member>>>> {
//                return nghruService.getMember(tokenManager.getToken().accessToken!!, householdId)
//            }
//        }.asLiveData()
//    }

}
