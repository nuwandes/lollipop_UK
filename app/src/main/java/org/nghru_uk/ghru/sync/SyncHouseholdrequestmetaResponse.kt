package org.nghru_uk.ghru.sync

import org.nghru_uk.ghru.vo.request.HouseholdRequestMeta

class SyncHouseholdrequestmetaResponse(
    val eventType: SyncResponseEventType,
    val householdRequestMeta: HouseholdRequestMeta
)