package org.nghru_uk.ghru.jobs

import com.birbit.android.jobqueue.Job
import com.birbit.android.jobqueue.Params
import com.birbit.android.jobqueue.RetryConstraint
import org.nghru_uk.ghru.sync.SyncResponseEventType
import org.nghru_uk.ghru.sync.SyncSampleStorageRequestRxBus
import org.nghru_uk.ghru.vo.request.SampleStorageRequest
import timber.log.Timber

class SyncSampledStorageJob(private val sampleStorageRequest: SampleStorageRequest) : Job(
    Params(JobPriority.SAMPLE_STORAGE)
        .setRequiresNetwork(true)
        .groupBy("SampleSorage")
        .persist()
) {


    override fun onAdded() {
        Timber.d("Executing onAdded() for comment $sampleStorageRequest")
    }

    override fun shouldReRunOnThrowable(throwable: Throwable, runCount: Int, maxRunCount: Int): RetryConstraint {
        if (throwable is RemoteException) {

            val statusCode = throwable.response.code()
            if (statusCode >= 422 && statusCode < 500) {
                return RetryConstraint.CANCEL
            }
        }
        // if we are here, most likely the connection was lost during job execution
        return RetryConstraint.createExponentialBackoff(runCount, 1000);
    }

    override fun onRun() {
        Timber.d("Executing onRun() for household $sampleStorageRequest")
        RemoteHouseholdService().getInstance().addSampleStorageRequest(sampleStorageRequest)
        SyncSampleStorageRequestRxBus.getInstance().post(SyncResponseEventType.SUCCESS, sampleStorageRequest)
    }

    override fun onCancel(cancelReason: Int, throwable: Throwable?) {
        Timber.d("canceling job. reason: %d, throwable: %s", cancelReason, throwable)
        //Crashlytics.logException(throwable)
        //Crashlytics.log("sampleStorageRequest " + sampleStorageRequest.toString())
        // sync to remote failed
        SyncSampleStorageRequestRxBus.getInstance().post(SyncResponseEventType.FAILED, sampleStorageRequest)
    }
}