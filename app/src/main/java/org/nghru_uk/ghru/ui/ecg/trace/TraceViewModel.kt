package org.nghru_uk.ghru.ui.ecg.trace

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.nghru_uk.ghru.repository.ECGRepository
import org.nghru_uk.ghru.repository.StationDevicesRepository
import org.nghru_uk.ghru.ui.ecg.trace.complete.CompleteDialogViewModel
import org.nghru_uk.ghru.util.AbsentLiveData
import org.nghru_uk.ghru.vo.ECG
import org.nghru_uk.ghru.vo.Measurements
import org.nghru_uk.ghru.vo.Resource
import org.nghru_uk.ghru.vo.StationDeviceData
import org.nghru_uk.ghru.vo.request.ParticipantRequest
import javax.inject.Inject


class TraceViewModel
@Inject constructor(
    eCGRepository: ECGRepository,
    stationDevicesRepository: StationDevicesRepository
) : ViewModel() {

    //private val _participantRequestRemote: MutableLiveData<ParticipantRequest> = MutableLiveData()

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

//
//    var eCGSaveRemote: LiveData<Resource<ResourceData<ECG>>>? = Transformations
//            .switchMap(_participantRequestRemote) { participant ->
//                if (participant == null) {
//                    AbsentLiveData.create()
//                } else {
//                    eCGRepository.syncECG(participant)
//                }
//            }
//
//    fun setECGRemote(participantRequest: ParticipantRequest) {
//        if (_participantRequestRemote.value != participantRequest) {
//            _participantRequestRemote.postValue(participantRequest)
//        }
//    }

    // after remove trace  status -------------------- 05-02-2021 -----------------

    private val _participantRequestRemote: MutableLiveData<ECGId> = MutableLiveData()
    private var isOnline : Boolean = false

    var eCGSaveRemote: LiveData<Resource<ECG>>? = Transformations
        .switchMap(_participantRequestRemote) { input ->
            input.ifExists { participantRequest, comment, device_id ->
                eCGRepository.syncECG(participantRequest, comment, device_id,isOnline)
            }
        }

    data class ECGId(
        val participantRequest: ParticipantRequest?,
        val comment: String?,
        val device_id: String
    ) {
        fun <T> ifExists(f: (ParticipantRequest, String?, String) -> LiveData<T>): LiveData<T> {
            return if (participantRequest == null) {
                AbsentLiveData.create()
            } else {
                f(participantRequest, comment!!, device_id)
            }
        }
    }

    fun setECGRemote(participantRequest: ParticipantRequest, comment: String?, device_id: String, online : Boolean) {

        isOnline = online
        val update = ECGId(participantRequest, comment, device_id)
        if (_participantRequestRemote.value == update) {
            return
        }
        _participantRequestRemote.value = update
    }

    // ----------------------------------------------------------------------------
}
