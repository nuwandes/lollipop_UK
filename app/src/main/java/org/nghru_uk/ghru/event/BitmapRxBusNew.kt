package org.nghru_uk.ghru.event

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import org.nghru_uk.ghru.vo.SavedBitMap


class BitmapRxBusNew private constructor() {
    private val relay: PublishRelay<SavedBitMap>

    init {
        relay = PublishRelay.create()
    }

    fun post(bitmap: SavedBitMap) {
        relay.accept(bitmap)
    }

    fun toObservable(): Observable<SavedBitMap> {
        return relay
    }

    companion object {

        private var instance: BitmapRxBusNew? = null

        @Synchronized
        fun getInstance(): BitmapRxBusNew {
            if (instance == null) {
                instance = BitmapRxBusNew()
            }
            return instance as BitmapRxBusNew
        }
    }
}