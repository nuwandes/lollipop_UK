package org.nghru_uk.ghru.repository

import androidx.lifecycle.LiveData
import org.nghru_uk.ghru.AppExecutors
import org.nghru_uk.ghru.api.ApiResponse
import org.nghru_uk.ghru.api.NghruService
import org.nghru_uk.ghru.db.SampleStorageRequestDao
import org.nghru_uk.ghru.vo.Resource
import org.nghru_uk.ghru.vo.ResourceData
import org.nghru_uk.ghru.vo.SampleData
import org.nghru_uk.ghru.vo.request.SampleStorageRequest
import java.io.Serializable
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Repository that handles User objects.
 */

@Singleton
class SampleStorageRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    private val nghruService: NghruService,
    private val sampleStorageRequestDao: SampleStorageRequestDao
) : Serializable {


    fun syncSampleStorageRequest(
        sampleStorageRequest: SampleStorageRequest

    ): LiveData<Resource<ResourceData<SampleData>>> {
        return object : NetworkOnlyBoundResource<ResourceData<SampleData>>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<ResourceData<SampleData>>> {
                return nghruService.addSampleStorageSync(sampleStorageRequest.sampleId, sampleStorageRequest)
            }
        }.asLiveData()
    }

    fun insertSampleRequest(
        sampleStorageRequest: SampleStorageRequest
    ): LiveData<Resource<SampleStorageRequest>> {
        return object : LocalBoundInsertResource<SampleStorageRequest>(appExecutors) {
            override fun loadFromDb(rowId: Long): LiveData<SampleStorageRequest> {
                return sampleStorageRequestDao.getSampleStorageRequest(rowId)
            }

            override fun insertDb(): Long {
                return sampleStorageRequestDao.insert(sampleStorageRequest)
            }
        }.asLiveData()
    }


    fun syncSampleStorageFRequest(
        sampleStorageRequest: SampleStorageRequest

    ): LiveData<Resource<ResourceData<SampleData>>> {
        return object : NetworkOnlyBoundResource<ResourceData<SampleData>>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<ResourceData<SampleData>>> {
                return nghruService.addSampleStorageSync(sampleStorageRequest.sampleId, sampleStorageRequest)
            }
        }.asLiveData()
    }

    fun insertSampleStorageRequest(
        sampleStorageRequest: SampleStorageRequest
    ): LiveData<Resource<SampleStorageRequest>> {
        return object : LocalBoundInsertResource<SampleStorageRequest>(appExecutors) {
            override fun loadFromDb(rowId: Long): LiveData<SampleStorageRequest> {
                return sampleStorageRequestDao.getSampleStorageRequest(rowId)
            }

            override fun insertDb(): Long {
                return sampleStorageRequestDao.insert(sampleStorageRequest)
            }
        }.asLiveData()
    }

}
