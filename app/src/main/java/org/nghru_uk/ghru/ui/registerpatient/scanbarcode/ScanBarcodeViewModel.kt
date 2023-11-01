package org.nghru_uk.ghru.ui.registerpatient.scanbarcode

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.nghru_uk.ghru.repository.ParticipantMetaRepository
import org.nghru_uk.ghru.util.AbsentLiveData
import org.nghru_uk.ghru.vo.Resource
import org.nghru_uk.ghru.vo.request.ParticipantRequest
import javax.inject.Inject


class ScanBarcodeViewModel
@Inject constructor(participantMetaRepository: ParticipantMetaRepository) : ViewModel() {

    private val _screeningId: MutableLiveData<String> = MutableLiveData()

    var screeningIdCheck: LiveData<Resource<ParticipantRequest>>? = Transformations
        .switchMap(_screeningId) { screeningId ->
            if (screeningId == null) {
                AbsentLiveData.create()
            } else {
                participantMetaRepository.getItemId(screeningId)
            }
        }

    fun setScreeningId(screeningId: String?) {
        _screeningId.value = screeningId
    }
}
