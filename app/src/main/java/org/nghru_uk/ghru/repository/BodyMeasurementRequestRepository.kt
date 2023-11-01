package org.nghru_uk.ghru.repository

import androidx.lifecycle.LiveData
import org.nghru_uk.ghru.AppExecutors
import org.nghru_uk.ghru.api.ApiResponse
import org.nghru_uk.ghru.api.NghruService
import org.nghru_uk.ghru.db.BodyMeasurementMetaDao
import org.nghru_uk.ghru.db.BodyMeasurementRequestDao
import org.nghru_uk.ghru.vo.Message
import org.nghru_uk.ghru.vo.Resource
import org.nghru_uk.ghru.vo.ResourceData
import org.nghru_uk.ghru.vo.request.BodyMeasurementMeta
import org.nghru_uk.ghru.vo.request.BodyMeasurementMetaResonce
import org.nghru_uk.ghru.vo.request.BodyMeasurementRequest
import org.nghru_uk.ghru.vo.request.ParticipantRequest
import java.io.Serializable
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Repository that handles User objects.
 */

@Singleton
class BodyMeasurementRequestRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    private val bodyMeasurementRequestDao: BodyMeasurementRequestDao,
    //  private val ploodPresureRequestDao: BloodPresureItemRequestDao,
    private val bodyMeasurementMetaDao: BodyMeasurementMetaDao,
    private val nghruService: NghruService
) : Serializable {

    fun syncBodyMeasurementRequest(
        bodyMeasurementRequest: BodyMeasurementRequest,
        particap: ParticipantRequest
    ): LiveData<Resource<ResourceData<BodyMeasurementRequest>>> {
        return object : NetworkOnlyBoundResource<ResourceData<BodyMeasurementRequest>>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<ResourceData<BodyMeasurementRequest>>> {
                return nghruService.addBodyMeasurementRequest(particap.screeningId, bodyMeasurementRequest)
            }
        }.asLiveData()
    }


    fun syncBodyMeasurementMeta(
        bodyMeasurementRequest: BodyMeasurementMeta,
        particap: ParticipantRequest
    ): LiveData<Resource<ResourceData<Message>>> {
        return object : NetworkOnlyBoundResource<ResourceData<Message>>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<ResourceData<Message>>> {
                return nghruService.addBodyMeasurementMeta(particap.screeningId, bodyMeasurementRequest)
            }
        }.asLiveData()
    }

    fun getBodyMeasurementMetaLocal(
        particap: ParticipantRequest
    ): LiveData<Resource<BodyMeasurementMetaResonce>> {
        return object : NetworkOnlyBoundResource<BodyMeasurementMetaResonce>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<BodyMeasurementMetaResonce>> {
                return nghruService.getBodyMeasurementMeta(particap.screeningId)
            }
        }.asLiveData()
    }

    fun getBodyMeasurementMeta(
        particap: ParticipantRequest,
        isOnline : Boolean
    ): LiveData<Resource<BodyMeasurementMeta>> {
        return object : NetworkBoundResource<BodyMeasurementMeta, BodyMeasurementMetaResonce>(appExecutors) {
            override fun saveCallResult(item: BodyMeasurementMetaResonce) {
                val bodyMeasurementMeta = item.data?.station?.data!!
                bodyMeasurementMeta.screeningId = particap.screeningId
                bodyMeasurementMetaDao.insert(bodyMeasurementMeta)
            }

            override fun shouldFetch(data: BodyMeasurementMeta?) = isOnline

            override fun loadFromDb(): LiveData<BodyMeasurementMeta> {
               return bodyMeasurementMetaDao.getBodyMeasurementMetas(particap.screeningId)
            }

            override fun createCall(): LiveData<ApiResponse<BodyMeasurementMetaResonce>> {
                return nghruService.getBodyMeasurementMeta(particap.screeningId)
            }
        }.asLiveData()
    }


    fun insertBodyMeasurementRequest(
        participantRequest: BodyMeasurementRequest
    ): LiveData<Resource<BodyMeasurementRequest>> {
        return object : LocalBoundInsertResource<BodyMeasurementRequest>(appExecutors) {
            override fun loadFromDb(rowId: Long): LiveData<BodyMeasurementRequest> {
                return bodyMeasurementRequestDao.getBodyMeasurementRequest(rowId)
            }

            override fun insertDb(): Long {
                return bodyMeasurementRequestDao.insert(participantRequest)
            }
        }.asLiveData()
    }


//    fun insertBPs(
//            members: List<BloodPresureItemRequest>,
//            bodyMeasurementRequestId: BodyMeasurementRequest
//    ): LiveData<Resource<List<BloodPresureItemRequest>>> {
//        return object : LocalBoundInsertAllResource<List<BloodPresureItemRequest>>(appExecutors) {
//            override fun loadFromDb(): LiveData<List<BloodPresureItemRequest>> {
//                return ploodPresureRequestDao.getBloodPressureRequest(bodyMeasurementRequestId.id)
//            }
//
//            override fun insertDb(): Unit {
//                return ploodPresureRequestDao.insertAll(members)
//            }
//        }.asLiveData()
//    }
}
