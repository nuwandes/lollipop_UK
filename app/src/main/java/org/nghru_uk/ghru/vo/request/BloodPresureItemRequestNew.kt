package org.nghru_uk.ghru.vo.request

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(tableName = "blood_pressure_item_request_new")
data class BloodPresureItemRequestNew(

    @Expose @SerializedName("systolic") @ColumnInfo(name = "systolic") val systolic: Int,
    @Expose @SerializedName("diastolic") @ColumnInfo(name = "diastolic") val diastolic: Int,
    @Expose @SerializedName("pulse") @ColumnInfo(name = "pulse") val pulse: Int,
    @Expose @SerializedName("timestamp") @ColumnInfo(name = "timestamp") val timestamp: String

) : Serializable, Parcelable {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    @ColumnInfo(name = "blood_presure_request_id")
    var bloodPresureRequestId: Long = 0

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString()!!
    ) {
        id = parcel.readLong()

        bloodPresureRequestId = parcel.readLong()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(systolic)
        parcel.writeInt(diastolic)
        parcel.writeInt(pulse)
        parcel.writeString(timestamp)

        parcel.writeLong(id)

        parcel.writeLong(bloodPresureRequestId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BloodPresureItemRequestNew> {
        override fun createFromParcel(parcel: Parcel): BloodPresureItemRequestNew {
            return BloodPresureItemRequestNew(parcel)
        }

        override fun newArray(size: Int): Array<BloodPresureItemRequestNew?> {
            return arrayOfNulls(size)
        }
    }


}