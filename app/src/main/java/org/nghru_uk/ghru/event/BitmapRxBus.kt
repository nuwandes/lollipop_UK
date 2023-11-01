package org.nghru_uk.ghru.event

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import org.nghru_uk.ghru.vo.SavedBitMap


class BitmapRxBus private constructor() {
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

        private var instance: BitmapRxBus? = null

        @Synchronized
        fun getInstance(): BitmapRxBus {
            if (instance == null) {
                instance = BitmapRxBus()
            }
            return instance as BitmapRxBus
        }
    }
}