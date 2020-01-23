
package com.app.detection.model.SdkLicense;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("sdk")
    @Expose
    private Sdk sdk;
    @SerializedName("total_licence")
    @Expose
    private Integer totalLicence;

    public Sdk getSdk() {
        return sdk;
    }

    public void setSdk(Sdk sdk) {
        this.sdk = sdk;
    }

    public Integer getTotalLicence() {
        return totalLicence;
    }

    public void setTotalLicence(Integer totalLicence) {
        this.totalLicence = totalLicence;
    }

}
