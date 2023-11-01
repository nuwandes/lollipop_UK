package org.nghru_uk.ghru.vo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.nghru_uk.ghru.vo.request.HouseholdRequest;

public class HouseholdData {
    @Expose
    @SerializedName("household")
    HouseholdRequest household;
    @Expose
    @SerializedName("metadata")
    Metadata metadata;
    @Expose
    @SerializedName("error")
    String error;
    @Expose
    @SerializedName("message")
    String message;
}

