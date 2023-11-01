package org.nghru_uk.ghru.sync

import org.nghru_uk.ghru.vo.ECGStatus


class ECGStatusRequestResponse(
    val eventType: SyncResponseEventType,
    val ecgStatus: ECGStatus) {

}