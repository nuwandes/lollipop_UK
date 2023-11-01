package org.nghru_uk.ghru.repository

import androidx.lifecycle.LiveData
import com.birbit.android.jobqueue.JobManager
import org.nghru_uk.ghru.AppExecutors
import org.nghru_uk.ghru.api.ApiResponse
import org.nghru_uk.ghru.api.NghruService
import org.nghru_uk.ghru.db.ECGStatusDao
import org.nghru_uk.ghru.db.MetaNewDao
import org.nghru_uk.ghru.jobs.SyncECGJob
import org.nghru_uk.ghru.vo.*
import org.nghru_uk.ghru.vo.request.ParticipantRequest
import java.io.Serializable
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Repository that handles User objects.
 */

@Singleton
class ECGRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    private val nghruService: NghruService,
    private val jobManager: JobManager,
    private val metaNewDao: MetaNewDao,
    private val ecgStatusDao: ECGStatusDao
) : Serializable {

//    fun syncECG(
//        participantRequest: ParticipantRequest,
//        status: String,
//        comment: String?,
//        device_id: String,
//        isOnline : Boolean
//    ): LiveData<Resource<ECG>> {
//        return object : MyNetworkBoundResource<ECG,ResourceData<ECG>>(appExecutors) {
//
//            override fun createJob(insertedID: Long) {
//                val mECGStatus = ECGStatus(status, comment, device_id, meta= participantRequest?.meta)
//                mECGStatus.syncPending = !isOnline
//                mECGStatus.id = insertedID
//                jobManager.addJobInBackground(
//
//                    SyncECGJob(participantRequest, mECGStatus)
//                )
//            }
//            override fun isNetworkAvilable(): Boolean {
//
//                return !isOnline
//            }
//            override fun saveDb(): Long {
//                val mECGStatus = ECGStatus(status, comment, device_id, meta= participantRequest?.meta)
//                mECGStatus.syncPending = !isOnline
//                var ecgMetaNewId = metaNewDao.insert(participantRequest?.meta!!)
//                mECGStatus.metaId = ecgMetaNewId
//                mECGStatus.screeningId = participantRequest?.screeningId
//                return  ecgStatusDao.insert(mECGStatus)
//            }
//            override fun createCall(): LiveData<ApiResponse<ResourceData<ECG>>> {
//                val mECGStatus = ECGStatus(status, comment, device_id, meta= participantRequest?.meta)
//                mECGStatus.syncPending = !isOnline
//                return nghruService.addECGSync(participantRequest.screeningId, mECGStatus)
//            }
//        }.asLiveData()
//    }

    fun syncECG(
        participantRequest: ParticipantRequest,
        comment: String?,
        device_id: String,
        isOnline : Boolean
    ): LiveData<Resource<ECG>> {
        return object : MyNetworkBoundResource<ECG,ResourceData<ECG>>(appExecutors) {

            override fun createJob(insertedID: Long) {
                val mECGStatus = ECGStatus(comment, device_id, meta= participantRequest?.meta)
                mECGStatus.syncPending = !isOnline
                mECGStatus.id = insertedID
                jobManager.addJobInBackground(

                    SyncECGJob(participantRequest, mECGStatus)
                )
            }
            override fun isNetworkAvilable(): Boolean {

                return !isOnline
            }
            override fun saveDb(): Long {
                val mECGStatus = ECGStatus(comment, device_id, meta= participantRequest?.meta)
                mECGStatus.syncPending = !isOnline
                var ecgMetaNewId = metaNewDao.insert(participantRequest?.meta!!)
                mECGStatus.metaId = ecgMetaNewId
                mECGStatus.screeningId = participantRequest?.screeningId
                return  ecgStatusDao.insert(mECGStatus)
            }
            override fun createCall(): LiveData<ApiResponse<ResourceData<ECG>>> {
                val mECGStatus = ECGStatus(comment, device_id, meta= participantRequest?.meta)
                mECGStatus.syncPending = !isOnline
                return nghruService.addECGSync(participantRequest.screeningId, mECGStatus)
            }
        }.asLiveData()
    }

//    fun getECGRequestFromLocalDB(
//
//    ): MutableLiveData<MutableList<ECGStatus>> {
//        return object : MyLocalBoundResource<MutableList<ECGStatus>>(appExecutors) {
//            override fun loadFromDb(): MutableLiveData<MutableList<ECGStatus>> {
//
//                var request = MutableLiveData<MutableList<ECGStatus>>()
//                var requestList : MutableList<ECGStatus> = ArrayList()
//                var ecgRequestList : List<ECGStatus> = ecgStatusDao.getECGStatusesSyncPending()
//                for(ecg in ecgRequestList)
//                {
//                    var ecgStatus : ECGStatus = ECGStatus(ecg.traceStatus,ecg.comment,ecg.device_id, metaNewDao.getNewMeta(ecg.metaId))
//                    ecgStatus.screeningId = ecg.screeningId
//                    requestList.add(ecgStatus)
//
//                }
//                request.postValue(requestList)
//                return request
//            }
//        }.asLiveData()
//    }

    fun getECGRequestFromLocalDB(

    ): LiveData<Resource<List<ECGStatus>>> {
        return object : LocalBoundResource<List<ECGStatus>>(appExecutors) {
            override fun loadFromDb(): LiveData<List<ECGStatus>> {
                return ecgStatusDao.getECGStatusesSyncPending()
            }
        }.asLiveData()
    }

    fun syncECGStatus(
        mECGStatus:ECGStatus
    ): LiveData<Resource<ResourceData<ECG>>> {
        return object : SyncNetworkOnlyBcakgroundBoundResource<ResourceData<ECG>>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<ResourceData<ECG>>> {
                return nghruService.addECGSync(mECGStatus.screeningId, mECGStatus)
            }

            override fun deleteCall() {
                return ecgStatusDao.deleteRequest(mECGStatus.id)
            }
        }.asLiveData()
    }
}
