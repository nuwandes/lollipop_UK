package org.nghru_uk.ghru.vo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.nghru_uk.ghru.vo.request.IntakeResponse

class IntakeData {

    @Expose
    @SerializedName("body")
    private val intake: IntakeResponse? = null

    fun getIntake(): IntakeResponse? {
        return intake
    }
}