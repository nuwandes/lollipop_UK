package org.nghru_uk.ghru.ui.fundoscopy.reading

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.nghru_uk.ghru.repository.AssertRepository
import org.nghru_uk.ghru.repository.FundoscopyRepository
import org.nghru_uk.ghru.repository.StationDevicesRepository
import org.nghru_uk.ghru.util.AbsentLiveData
import org.nghru_uk.ghru.vo.*
import org.nghru_uk.ghru.vo.request.ParticipantRequest
import javax.inject.Inject


class FundoscopyReadingViewModel
@Inject constructor(
    val assertRepository: AssertRepository,
    fundoscopyRepository: FundoscopyRepository,
    stationDevicesRepository: StationDevicesRepository
) : ViewModel() {
    var fundoscopySyncError: MutableLiveData<Boolean>? = MutableLiveData<Boolean>().apply { }


    private val _participantId: MutableLiveData<ParticipantRequest> = MutableLiveData()

    private val _participantIdComplte: MutableLiveData<ParticipantRequest> = MutableLiveData()
    private var comment: String? = null
    private var device_id: String? = null
    //private var pupil_dilation : Boolean = false
    private var isOnline : Boolean = false
    //private var cataractObservation : String? = null

    private val _stationName = MutableLiveData<String>()

    fun setStationName(stationName: Measurements) {
        val update = stationName.toString().toLowerCase()
        if (_stationName.value == update) {
            return
        }
        _stationName.value = update
    }

    var stationDeviceList: LiveData<Resource<List<StationDeviceData>>>? = Transformations
        .switchMap(_stationName) { input ->
            stationDevicesRepository.getStationDeviceList(_stationName.value!!)
        }

    var asserts: LiveData<Resource<ResourceData<List<Asset>>>>? = Transformations
        .switchMap(_participantId) { participantId ->
            if (participantId == null) {
                AbsentLiveData.create()
            } else {
                assertRepository.getAssets(participantId, "fundoscopy")
            }
        }

    var fundoscopyComplete: LiveData<Resource<ECG>>? = Transformations
        .switchMap(_participantIdComplte) { participantId ->
            if (participantId == null) {
                AbsentLiveData.create()
            } else {
                fundoscopyRepository.syncFundoscopy(participantId, comment, device_id,isOnline)
            }
        }

//    var fundoscopyComplete: LiveData<Resource<ECG>>? = Transformations
//        .switchMap(_participantIdComplte) { participantId ->
//            if (participantId == null) {
//                AbsentLiveData.create()
//            } else {
//                fundoscopyRepository.syncFundoscopy(participantId, comment, device_id,pupil_dilation,isOnline,cataractObservation!!)
//            }
//        }

//    fun setParticipant(participantId: ParticipantRequest, mComment: String?, mDevice_id: String,dilation: Boolean,observation : String) {
//        comment = mComment
//        device_id = mDevice_id
//        pupil_dilation = dilation
//        cataractObservation = observation
//
//        if (_participantId.value == participantId) {
//            return
//        }
//        _participantId.value = participantId
//    }

    fun setParticipant(participantId: ParticipantRequest, mComment: String?, mDevice_id: String) {
        comment = mComment
        device_id = mDevice_id

        if (_participantId.value == participantId) {
            return
        }
        _participantId.value = participantId
    }

//    fun setParticipantComplete(participantId: ParticipantRequest, mComment: String?, mDevice_id: String,dilation: Boolean,online: Boolean,observation : String) {
//        comment = mComment
//        device_id = mDevice_id
//        pupil_dilation = dilation
//        isOnline = online
//        cataractObservation = observation
//
//        if (_participantIdComplte.value == participantId) {
//            return
//        }
//        _participantIdComplte.value = participantId
//    }

    fun setParticipantComplete(participantId: ParticipantRequest, mComment: String?, mDevice_id: String,online: Boolean) {
        comment = mComment
        device_id = mDevice_id
        isOnline = online

        if (_participantIdComplte.value == participantId) {
            return
        }
        _participantIdComplte.value = participantId
    }
}
