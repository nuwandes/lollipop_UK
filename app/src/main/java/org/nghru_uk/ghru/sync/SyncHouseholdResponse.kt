package org.nghru_uk.ghru.sync

import org.nghru_uk.ghru.vo.request.HouseholdRequest

class SyncHouseholdResponse(val eventType: SyncResponseEventType, val household: HouseholdRequest)