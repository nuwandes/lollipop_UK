package org.nghru_uk.ghru.vo.request

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Ignore
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.nghru_uk.ghru.vo.Meta
import java.io.Serializable


data class SampleCreateRequest(
    @Expose @SerializedName("meta")  val meta: Meta?,
    @Expose @SerializedName("comment")  val comment: String?
) : Serializable, Parcelable {

    @Ignore
    @Expose
    @SerializedName("questions")
    var question: List<Map<String, String>>? = null

    constructor(parcel: Parcel) : this(
        parcel.readParcelable(Meta::class.java.classLoader),
        parcel.readString()

    ) {

        readConfirmQuestions(parcel)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(meta, flags)
        parcel.writeString(comment)
        parcel.writeList(question)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SampleCreateRequest> {
        override fun createFromParcel(parcel: Parcel): SampleCreateRequest {
            return SampleCreateRequest(parcel)
        }

        override fun newArray(size: Int): Array<SampleCreateRequest?> {
            return arrayOfNulls(size)
        }

        private fun readConfirmQuestions(parcel: Parcel): List<Map<String, String>> {
            val list = mutableListOf<Map<String, String>>()
            parcel.readList(list as List<*>, Map::class.java.classLoader)

            return list
        }
    }


}