package org.nghru_uk.ghru.sync

import org.nghru_uk.ghru.vo.request.HouseholdRequest

class SyncHouseholdRequestResponse(val eventType: SyncResponseEventType, val householdRequest: HouseholdRequest)