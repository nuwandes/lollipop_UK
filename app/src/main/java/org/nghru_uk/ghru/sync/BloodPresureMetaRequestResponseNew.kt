package org.nghru_uk.ghru.sync

import org.nghru_uk.ghru.vo.request.BloodPressureMetaRequestNew


class BloodPresureMetaRequestResponseNew(
    val eventType: SyncResponseEventType,
    val bloodPressureMetaRequest: BloodPressureMetaRequestNew
)