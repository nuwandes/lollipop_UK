package org.nghru_uk.ghru.sync

import org.nghru_uk.ghru.vo.request.SampleStorageRequest

class SyncSampleStorageRequestResponse(
    val eventType: SyncResponseEventType,
    val sampleStorageRequest: SampleStorageRequest
)