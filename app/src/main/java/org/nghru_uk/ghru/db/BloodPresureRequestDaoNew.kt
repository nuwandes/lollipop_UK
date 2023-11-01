package org.nghru_uk.ghru.db

import androidx.lifecycle.LiveData
import androidx.room.*
import org.nghru_uk.ghru.vo.request.BloodPresureRequestNew


@Dao
interface BloodPresureRequestDaoNew {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(bloodPresureRequest: BloodPresureRequestNew): Long

    @Insert
    fun insertAll(users: List<BloodPresureRequestNew>)

    @Query("UPDATE blood_pressure_request SET sync_pending = 0 WHERE sync_pending = 1 AND screening_id = :screeningId")
    fun update(screeningId: String): Int


    // @Delete
    // fun delete(bloodPresureRequest: BloodPresureRequest)

    @Query("DELETE FROM blood_pressure_request_new")
    fun deleteAll()

    @Query("DELETE FROM blood_pressure_request_new WHERE id = :id")
    fun deleteRequest(id : Long)

    @Query("SELECT * FROM blood_pressure_request_new WHERE id = :id")
    fun getBloodPressureRequest(id: Long): LiveData<BloodPresureRequestNew>

    @Query("SELECT * FROM blood_pressure_request_new")
    fun getBloodPressureRequests(): LiveData<List<BloodPresureRequestNew>>

    @Query("SELECT * FROM blood_pressure_request_new WHERE sync_pending = 1 ORDER BY id ASC")
    fun getAllBloodPressureRequestsSyncPending(): List<BloodPresureRequestNew>
}