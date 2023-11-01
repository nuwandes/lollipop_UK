package org.nghru_uk.ghru.vo.request

import android.os.Parcel
import android.os.Parcelable
import androidx.room.*
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.nghru_uk.ghru.vo.Meta
import java.io.Serializable

@Entity(tableName = "checkout_request")
data class CheckoutRequest(
    @Expose @field:SerializedName("comment") var comment: String?,
    @Embedded(prefix = "meta") @Expose @SerializedName("meta") var meta: Meta?,
    @Embedded(prefix = "bank_details") @Expose @SerializedName("bank_details") var bank_details: CheckoutData?
) :
    Serializable, Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readParcelable(CheckoutData::class.java.classLoader),
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
        parcel.writeParcelable(bank_details, flags)

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

data class CheckoutData(
    @Expose @field:SerializedName("holder_name") var holder_name: String?,
    @Expose @field:SerializedName("bank_name") var bank_name: String?,
    @Expose @field:SerializedName("account_number") var account_number: String?
): Serializable, Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString()

    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(holder_name)
        parcel.writeString(bank_name)
        parcel.writeString(account_number)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CheckoutData> {
        override fun createFromParcel(parcel: Parcel): CheckoutData {
            return CheckoutData(parcel)
        }

        override fun newArray(size: Int): Array<CheckoutData?> {
            return arrayOfNulls(size)
        }
    }

}