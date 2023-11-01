package org.nghru_uk.ghru.vo.request

import android.os.Parcel
import android.os.Parcelable
import androidx.room.*
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.nghru_uk.ghru.vo.Meta
import java.io.Serializable

@Entity(tableName = "checkout_request_uk")
data class CheckoutRequestUK(
    @Expose @field:SerializedName("comment") var comment: String?,
    @Embedded(prefix = "meta") @Expose @SerializedName("meta") var meta: Meta?
) :
    Serializable, Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readParcelable(Meta::class.java.classLoader)
    ) {
    }

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
    @ColumnInfo(name = "timestamp")
    var timestamp: Long = System.currentTimeMillis()

    @ColumnInfo(name = "sync_pending")
    var syncPending: Boolean = false

    @ColumnInfo(name = "screening_id")
    lateinit var screeningId: String

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(comment)
        parcel.writeParcelable(meta, flags)

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CheckoutRequest> {
        override fun createFromParcel(parcel: Parcel): CheckoutRequest {
            return CheckoutRequest(parcel)
        }

        override fun newArray(size: Int): Array<CheckoutRequest?> {
            return arrayOfNulls(size)
        }
    }
}