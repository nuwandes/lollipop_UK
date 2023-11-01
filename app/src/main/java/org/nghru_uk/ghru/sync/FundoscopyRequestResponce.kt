package org.nghru_uk.ghru.sync

import org.nghru_uk.ghru.vo.FundoscopyRequest

class FundoscopyRequestResponce(
    val eventType: SyncResponseEventType,
    val fundoscopyRequest: FundoscopyRequest
)
{
}