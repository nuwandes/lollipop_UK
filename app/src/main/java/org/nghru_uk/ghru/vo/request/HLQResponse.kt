package org.nghru_uk.ghru.vo.request


import android.os.Parcel
import android.os.Parcelable
import androidx.room.*
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.nghru_uk.ghru.vo.Meta
import java.io.Serializable

data class HLQResponse(
    @Expose @field:SerializedName("questionnaire_completed") var questionnaire_completed: Boolean = false,
    @Expose @field:SerializedName("assistance_required") var assistance_required: Boolean = false,
    @Expose @field:SerializedName("meta") val meta: Meta?

) : Serializable, Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readParcelable(Meta::class.java.classLoader)
    ) {
    }


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(meta, flags)
        parcel.writeByte(if (questionnaire_completed) 1 else 0)
        parcel.writeByte(if (assistance_required) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<HLQResponse> {
        override fun createFromParcel(parcel: Parcel): HLQResponse {
            return HLQResponse(parcel)
        }

        override fun newArray(size: Int): Array<HLQResponse?> {
            return arrayOfNulls(size)
        }
    }

}