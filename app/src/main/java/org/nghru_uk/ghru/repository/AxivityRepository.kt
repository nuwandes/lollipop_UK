package org.nghru_uk.ghru.repository

import androidx.lifecycle.LiveData
import com.birbit.android.jobqueue.JobManager
import org.nghru_uk.ghru.AppExecutors
import org.nghru_uk.ghru.api.ApiResponse
import org.nghru_uk.ghru.api.NghruService
import org.nghru_uk.ghru.db.AxivityDao
import org.nghru_uk.ghru.jobs.SyncAxivityJob
import org.nghru_uk.ghru.vo.*
import org.nghru_uk.ghru.vo.request.ParticipantRequest
import java.io.Serializable
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Repository that handles User objects.
 */

@Singleton
class AxivityRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    private val nghruService: NghruService,
    private val jobManager: JobManager,
    private val axivityDao: AxivityDao
    ) : Serializable {

    fun syncAxivity(
        participantId: ParticipantRequest, axivity: Axivity?
    ): LiveData<Resource<Message>> {
        return object : MyNetworkBoundResource<Message,ResourceData<Message>>(appExecutors) {

            override fun createJob(insertedID: Long) {
                axivity?.id = insertedID
                jobManager.addJobInBackground(SyncAxivityJob(participantId?.screeningId!!, axivity!!))
            }
            override fun isNetworkAvilable(): Boolean {

                return axivity?.syncPending!!
            }
            override fun saveDb(): Long {

                return axivityDao.insert(axivity!!)
            }
            override fun createCall(): LiveData<ApiResponse<ResourceData<Message>>> {
                // val mECGStatus = ECGStatus(status, comment)
                return nghruService.addAxivity(participantId.screeningId, axivity!!)
            }

        }.asLiveData()
    }

    fun getAxivityListFromLocalDB(

    ): LiveData<Resource<List<Axivity>>> {
        return object : LocalBoundResource<List<Axivity>>(appExecutors) {
            override fun loadFromDb(): LiveData<List<Axivity>> {
                return axivityDao.getAxivityRequestSyncPending()
            }
        }.asLiveData()
    }
    fun syncAxivityRequest(
        axivity: Axivity?,
        screeningId : String
    ): LiveData<Resource<ResourceData<Message>>>  {
        return object : SyncNetworkOnlyBcakgroundBoundResource<ResourceData<Message>>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<ResourceData<Message>>>  {
                return nghruService.addAxivity(screeningId, axivity!!)
            }

            override fun deleteCall() {

                axivityDao.deleteRequest(axivity?.id!!)
            }
        }.asLiveData()
    }
}
