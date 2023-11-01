package org.nghru_uk.ghru.db

import androidx.lifecycle.LiveData
import androidx.room.*
import org.nghru_uk.ghru.vo.request.BloodPresureItemRequest
import org.nghru_uk.ghru.vo.request.BloodPresureItemRequestNew

/**
 * Interface for database access for User related operations.
 */
@Dao
interface BloodPresureItemRequestDaoNew {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(bloodPressureRequest: BloodPresureItemRequestNew): Long

    @Insert
    fun insertAll(users: List<BloodPresureItemRequestNew>)

    @Update
    fun update(bloodPressureRequest: BloodPresureItemRequestNew): Int

    @Delete
    fun delete(bloodPressureRequest: BloodPresureItemRequestNew)

    @Query("DELETE FROM blood_pressure_item_request_new")
    fun deleteAll()

    @Query("SELECT * FROM blood_pressure_item_request_new WHERE id = :id")
    fun getBloodPressureItemRequest(id: Long): LiveData<BloodPresureItemRequestNew>

    @Query("SELECT * FROM blood_pressure_item_request_new")
    fun getBloodPressureAllRequests(): LiveData<List<BloodPresureItemRequestNew>>

    @Query("SELECT * FROM blood_pressure_item_request_new WHERE blood_presure_request_id = :bloodPressureRequestId")
    fun getBloodPressureItemsByBloodPresureRequestID(bloodPressureRequestId: Long): List<BloodPresureItemRequestNew>


}
