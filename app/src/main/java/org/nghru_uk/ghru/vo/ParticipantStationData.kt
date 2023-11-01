package org.nghru_uk.ghru.vo

import androidx.room.ColumnInfo
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ParticipantStationData<T>(
    @field:SerializedName(value = "stations")
    @Expose val stations: T?,
    @field:SerializedName(value = "screening_id")
    @Expose val screening_id: T?,
    @Expose @field:SerializedName(value = "message")
    val message: String,
    @Expose @field:SerializedName(value = "error")
    val error: Boolean,
    @Expose @SerializedName("station_status")
    @ColumnInfo(name = "station_status")
    val stationStatus: Boolean
)