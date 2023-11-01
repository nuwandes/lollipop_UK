package org.nghru_uk.ghru.vo.request

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(tableName = "blood_pressure_request_new")
data class BloodPresureRequestNew(

    @Expose @SerializedName("comment") @ColumnInfo(name = "comment") val comment: String,
    @Expose @SerializedName("device_id") @ColumnInfo(name = "device_id") val device_id: String

) : Serializable, Parcelable {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    @Ignore
    @Expose
    @SerializedName("blood_pressure")
    var bloodPresureRequestList: List<BloodPresureItemRequestNew>? = null

    @ColumnInfo(name = "timestamp")
    var timestamp: Long = System.currentTimeMillis()

    @ColumnInfo(name = "sync_pending")
    var syncPending: Boolean = false

    @ColumnInfo(name = "screening_id")
    lateinit var screeningId: String

    @ColumnInfo(name = "meta_id")
    var metaId: Long = 0

    @Ignore
    @Expose
    @SerializedName("arm")
    lateinit var arm: String

    @Ignore
    @Expose
    @SerializedName("cuff_size")
    lateinit var cuffSize: String

    @Ignore
    @Expose
    @SerializedName("smoking")
    lateinit var smoking: String

    @Ignore
    @Expose
    @SerializedName("caffeine_consumption")
    lateinit var caffeineConsumption: String

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString()
    ) {
        id = parcel.readLong()
        bloodPresureRequestList = parcel.createTypedArrayList(BloodPresureItemRequestNew)
        timestamp = parcel.readLong()
        syncPending = parcel.readByte() != 0.toByte()
        screeningId  = parcel.readString()
        metaId = parcel.readLong()
        arm  = parcel.readString()
        cuffSize  = parcel.readString()
        smoking  = parcel.readString()
        caffeineConsumption  = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {

        parcel.writeString(comment)
        parcel.writeString(device_id)
        parcel.writeLong(id)
        parcel.writeTypedList(bloodPresureRequestList)
        parcel.writeLong(timestamp)
        parcel.writeByte(if (syncPending) 1 else 0)
        parcel.writeString(screeningId)
        parcel.writeLong(metaId)
        parcel.writeString(arm)
        parcel.writeString(cuffSize)
        parcel.writeString(smoking)
        parcel.writeString(caffeineConsumption)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BloodPresureRequestNew> {
        override fun createFromParcel(parcel: Parcel): BloodPresureRequestNew {
            return BloodPresureRequestNew(parcel)
        }

        override fun newArray(size: Int): Array<BloodPresureRequestNew?> {
            return arrayOfNulls(size)
        }
    }
}