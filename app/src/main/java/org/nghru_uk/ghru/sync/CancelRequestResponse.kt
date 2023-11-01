package org.nghru_uk.ghru.sync

import org.nghru_uk.ghru.vo.request.CancelRequest

class CancelRequestResponse( val eventType: SyncResponseEventType,
                             val cancelRequest: CancelRequest
) {
}