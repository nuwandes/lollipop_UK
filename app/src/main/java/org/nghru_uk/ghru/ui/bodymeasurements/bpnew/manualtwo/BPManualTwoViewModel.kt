package org.nghru_uk.ghru.ui.bodymeasurements.bpnew.manualtwo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.nghru_uk.ghru.vo.Arm
import org.nghru_uk.ghru.vo.BloodPressure
import org.nghru_uk.ghru.vo.BodyMeasurement
import javax.inject.Inject


class BPManualTwoViewModel
@Inject constructor() : ViewModel() {

    var bodyMeasurement: MutableLiveData<BodyMeasurement>? = MutableLiveData<BodyMeasurement>()

    var bloodPressure: MutableLiveData<BloodPressure>? = null

    var isValidSystolicBp: Boolean = false

    var isValidDiastolicBp: Boolean = false

    var isValidPuls: Boolean = false

    fun setBodyMeasurement(mesurment: BodyMeasurement) {
        bodyMeasurement?.value = mesurment
    }

    fun getBodyMeasurement(): LiveData<BodyMeasurement> {
        if (bodyMeasurement == null) {
            bodyMeasurement = MutableLiveData<BodyMeasurement>()
            loadBodyMeasurement()
        }
        return bodyMeasurement as MutableLiveData<BodyMeasurement>
    }

    fun setArm(arm: Arm) {
        bodyMeasurement?.value?.bloodPressures?.value!![2].arm.postValue(arm.name)
    }


    fun loadBodyMeasurement() {
        bodyMeasurement?.value = BodyMeasurement()
    }

    fun getBloodPressure(): LiveData<BloodPressure> {

        if (bloodPressure == null) {
            bloodPressure = MutableLiveData<BloodPressure>()
            bloodPressure?.value = BloodPressure(0)
        }

        return bloodPressure as MutableLiveData<BloodPressure>
    }


}
