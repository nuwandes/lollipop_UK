package org.nghru_uk.ghru.sync

import org.nghru_uk.ghru.vo.request.Member

class SyncHouseholdMemberListResponse(val eventType: SyncResponseEventType, val member: List<Member>)