package org.nghru_uk.ghru.sync

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import io.reactivex.disposables.CompositeDisposable
import org.nghru_uk.ghru.db.BloodPresureRequestDao
import org.nghru_uk.ghru.db.BloodPresureRequestDaoNew
import timber.log.Timber

class BloodPressureMetaRequestLifecycleObserverNew(var bloodPressureRequestDao: BloodPresureRequestDaoNew) :
    LifecycleObserver {
    private val disposables = CompositeDisposable()

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        Timber.d("onResume lifecycle event.")
        disposables.add(
            BloodPressureMetaRequestRxBusNew.getInstance().toObservable()
                .subscribe({ result ->
                    Log.d("Result", "household SyncBloodPressureMetaRequestObserver ${result.bloodPressureMetaRequest}")
                    handleSyncResponse(result)
                }, { error ->
                    error.printStackTrace()
                })
        )
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        Timber.d("onPause lifecycle event.")
        disposables.clear()
    }

    private fun handleSyncResponse(response: BloodPresureMetaRequestResponseNew) {
        if (response.eventType === SyncResponseEventType.SUCCESS) {
            onSyncCommentSuccess(response)
        } else {
            onSyncCommentFailed(response)
        }
    }

    private fun onSyncCommentSuccess(bloodPresureMetaRequest: BloodPresureMetaRequestResponseNew) {
        //Timber.d("received sync comment success event for comment %s", household.bodyMeasurementRequest)
        bloodPresureMetaRequest.bloodPressureMetaRequest.syncPending = false
        bloodPresureMetaRequest.bloodPressureMetaRequest.body.syncPending = false
        val udatedId=bloodPressureRequestDao.update(bloodPresureMetaRequest.bloodPressureMetaRequest.body.screeningId)
        //Timber.d("received sync comment success event for householdId %s", household.bodyMeasurementRequest)


    }

    private fun onSyncCommentFailed(household: BloodPresureMetaRequestResponseNew) {
        Timber.d("received sync comment failed event for comment %s", household)
//        disposables.add(deleteCommentUseCase.deleteComment(comment)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe({ Timber.d("delete comment success") },
//                        { t -> Timber.e(t, "delete comment error") }))
    }
}