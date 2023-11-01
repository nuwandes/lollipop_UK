package org.nghru_uk.ghru.ui.checkout.completion.paymentcompletion

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.nghru_uk.ghru.repository.ParticipantRepository
import org.nghru_uk.ghru.util.AbsentLiveData
import org.nghru_uk.ghru.vo.Resource
import org.nghru_uk.ghru.vo.ResourceData
import org.nghru_uk.ghru.vo.request.ParticipantRequest
import javax.inject.Inject

class PaymentCompletionViewModel
@Inject constructor(participantRepository: ParticipantRepository) : ViewModel() {

    private val _screeningId: MutableLiveData<String> = MutableLiveData()
    val screeningId: LiveData<String>
        get() = _screeningId

    private val _screeningIdOffline: MutableLiveData<String> = MutableLiveData()
    val screeningIdOffline: LiveData<String>
        get() = _screeningIdOffline

    var participantOffline: LiveData<Resource<ParticipantRequest>>? = Transformations
        .switchMap(_screeningIdOffline) { screeningIdOffline ->
            if (screeningIdOffline == null) {
                AbsentLiveData.create()
            } else {
                participantRepository.getParticipantOffline(screeningIdOffline)
            }
        }

    fun setScreeningIdOffline(screeningIdOffline: String?) {
        if (_screeningIdOffline.value == screeningIdOffline) {
            return
        }
        _screeningIdOffline.value = screeningIdOffline
    }

    var participant: LiveData<Resource<ResourceData<ParticipantRequest>>> = Transformations
        .switchMap(_screeningId) { screeningId ->
            if (screeningId == null) {
                AbsentLiveData.create()
            } else {
                participantRepository.getParticipantRequest(screeningId, "checkout")
            }
        }

    fun setScreeningId(screeningId: String?) {
        if (_screeningId.value == screeningId) {
            return
        }
        _screeningId.value = screeningId
    }
}