package org.nghru_uk.ghru.ui.checkout.notcomplete

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.nghru_uk.ghru.repository.CheckoutRepository
import org.nghru_uk.ghru.repository.HomeRepository
import org.nghru_uk.ghru.repository.ParticipantRepository
import org.nghru_uk.ghru.repository.UserRepository
import org.nghru_uk.ghru.util.AbsentLiveData
import org.nghru_uk.ghru.vo.Message
import org.nghru_uk.ghru.vo.Resource
import org.nghru_uk.ghru.vo.ResourceData
import org.nghru_uk.ghru.vo.request.CheckoutRequestUK
import javax.inject.Inject


class NotCompleteDialogViewModel
@Inject constructor(checkoutRepository: CheckoutRepository) : ViewModel() {

    //    Post checkout ------------------------------------------------------------------------------------------

    private val _chkPostRequest: MutableLiveData<CheckoutRequestUK> = MutableLiveData()
    private var _participant: String? = null

    fun setPostChk(cogRequest: CheckoutRequestUK, participantId: String?) {
        _participant = participantId
        if (_chkPostRequest.value == cogRequest) {
            return
        }
        _chkPostRequest.value = cogRequest
    }

    var chkPostComplete: LiveData<Resource<ResourceData<Message>>>? = Transformations
        .switchMap(_chkPostRequest) { cogPostRequest ->
            if (cogPostRequest == null) {
                AbsentLiveData.create()
            } else {
                checkoutRepository.syncChkUK(cogPostRequest,_participant!!)
            }
        }

    // ----------------------------------------------------------------------------

}
