package org.nghru_uk.ghru.repository

import androidx.lifecycle.LiveData
import org.nghru_uk.ghru.AppExecutors
import org.nghru_uk.ghru.api.ApiResponse
import org.nghru_uk.ghru.api.NghruService
import org.nghru_uk.ghru.db.StationDevicesDao
import org.nghru_uk.ghru.vo.Resource
import org.nghru_uk.ghru.vo.ResourceData
import org.nghru_uk.ghru.vo.StationDeviceData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StationDevicesRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    private val nghruService: NghruService,
    private val stationDevicesDao: StationDevicesDao

) {

    fun loadStationDevices(): LiveData<Resource<ResourceData<List<StationDeviceData>>>> {

        return object : NetworkOnlyBoundResource<ResourceData<List<StationDeviceData>>>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<ResourceData<List<StationDeviceData>>>> {
                return nghruService.getStationDevices()
            }
        }.asLiveData()
    }

//    fun loadHemoDevices(): LiveData<Resource<ResourceData<List<StationDeviceData>>>> {
//
//        return object : NetworkOnlyBoundResource<ResourceData<List<StationDeviceData>>>(appExecutors) {
//            override fun createCall(): LiveData<ApiResponse<ResourceData<List<StationDeviceData>>>> {
//                return nghruService.getHemoglobinStationDevices()
//            }
//        }.asLiveData()
//    }

    fun insertStationDeviceList(stationDevicesList: List<StationDeviceData>):
            LiveData<Resource<List<StationDeviceData>>> {
        stationDevicesDao.deleteAll()
        return object : LocalBoundInsertAllResource<List<StationDeviceData>>(appExecutors) {

            override fun loadFromDb(): LiveData<List<StationDeviceData>> {
                return stationDevicesDao.getAllDevice()
            }

            override fun insertDb(): Unit {

                return stationDevicesDao.insertAll(stationDevicesList)
            }
        }.asLiveData()
    }

    fun getStationDeviceList(
        measurement: String
    ): LiveData<Resource<List<StationDeviceData>>> {
        return object : LocalBoundResource<List<StationDeviceData>>(appExecutors) {
            override fun loadFromDb(): LiveData<List<StationDeviceData>> {
                return stationDevicesDao.stationDeviceList(measurement)
            }
        }.asLiveData()
    }

}