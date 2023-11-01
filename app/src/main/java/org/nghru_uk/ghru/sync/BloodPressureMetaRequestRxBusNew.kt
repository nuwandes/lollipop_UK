package org.nghru_uk.ghru.sync

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import org.nghru_uk.ghru.vo.request.BloodPressureMetaRequest
import org.nghru_uk.ghru.vo.request.BloodPressureMetaRequestNew


class BloodPressureMetaRequestRxBusNew private constructor() {

    private val relay: PublishRelay<BloodPresureMetaRequestResponseNew>

    init {
        relay = PublishRelay.create()
    }

    fun post(eventType: SyncResponseEventType, bloodPressureMetaRequest: BloodPressureMetaRequestNew) {
        relay.accept(BloodPresureMetaRequestResponseNew(eventType, bloodPressureMetaRequest))
    }

    fun toObservable(): Observable<BloodPresureMetaRequestResponseNew> {
        return relay
    }

    companion object {

        private var instance: BloodPressureMetaRequestRxBusNew? = null

        @Synchronized
        fun getInstance(): BloodPressureMetaRequestRxBusNew {
            if (instance == null) {
                instance = BloodPressureMetaRequestRxBusNew()
            }
            return instance as BloodPressureMetaRequestRxBusNew
        }
    }
}