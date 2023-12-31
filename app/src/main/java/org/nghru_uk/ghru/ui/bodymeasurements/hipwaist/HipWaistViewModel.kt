package org.nghru_uk.ghru.ui.bodymeasurements.hipwaist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.nghru_uk.ghru.repository.StationDevicesRepository
import org.nghru_uk.ghru.vo.Measurements
import org.nghru_uk.ghru.vo.Resource
import org.nghru_uk.ghru.vo.StationDeviceData
import javax.inject.Inject


class HipWaistViewModel
@Inject constructor(stationDevicesRepository: StationDevicesRepository) : ViewModel() {


    var isValidateError: Boolean = false

    var isValidHipSize: Boolean = false

    var isValidWaistSize: Boolean = false

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
}
