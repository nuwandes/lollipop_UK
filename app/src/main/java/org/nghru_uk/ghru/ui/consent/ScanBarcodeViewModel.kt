package org.nghru_uk.ghru.ui.consent

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.nghru_uk.ghru.repository.AssertRepository
import org.nghru_uk.ghru.repository.ParticipantRepository
import org.nghru_uk.ghru.repository.UserRepository
import org.nghru_uk.ghru.util.AbsentLiveData
import org.nghru_uk.ghru.vo.*
import org.nghru_uk.ghru.vo.request.ParticipantRequest
import javax.inject.Inject


class ScanBarcodeViewModel
@Inject constructor(participantRepository: ParticipantRepository,
                    userRepository: UserRepository,
                    assetRepository: AssertRepository
) : ViewModel() {

    private val _screeningId: MutableLiveData<String> = MutableLiveData()

    private val _screeningIdOffline: MutableLiveData<String> = MutableLiveData()

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
    private val _email = MutableLiveData<String>()

    val user: LiveData<Resource<User>>? = Transformations
        .switchMap(_email) { emailx ->
            if (emailx == null) {
                AbsentLiveData.create()
            } else {
                userRepository.loadUserDB()
            }
        }
    fun setUser(email: String?) {
        if (_email.value != email) {
            _email.value = email
        }
    }

    // ------------- to get the assets status ----------------------------------------------------------------

    private val _participantRequestId: MutableLiveData<String> = MutableLiveData()

    fun setParticipantForGetAsset(participantId: String) {

        if (_participantRequestId.value == participantId) {
            return
        }
        _participantRequestId.value = participantId
    }

    var getAssets: LiveData<Resource<ResourceData<List<Asset>>>>? = Transformations
        .switchMap(_participantRequestId) { participantId ->
            if (participantId == null) {
                AbsentLiveData.create()
            } else {
                assetRepository.getConsentAssets(participantId, "consent")
            }
        }
}
