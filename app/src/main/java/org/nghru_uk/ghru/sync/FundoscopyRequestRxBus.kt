package org.nghru_uk.ghru.sync

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import org.nghru_uk.ghru.vo.FundoscopyRequest

class FundoscopyRequestRxBus private constructor() {

    private val relay: PublishRelay<FundoscopyRequestResponce>

    init {
        relay = PublishRelay.create()
    }

    fun post(eventType: SyncResponseEventType, fundoscopyRequest: FundoscopyRequest) {
        relay.accept(FundoscopyRequestResponce(eventType, fundoscopyRequest))
    }

    fun toObservable(): Observable<FundoscopyRequestResponce> {
        return relay
    }

    companion object {

        private var instance: FundoscopyRequestRxBus? = null

        @Synchronized
        fun getInstance(): FundoscopyRequestRxBus {
            if (instance == null) {
                instance = FundoscopyRequestRxBus()
            }
            return instance as FundoscopyRequestRxBus
        }
    }
}