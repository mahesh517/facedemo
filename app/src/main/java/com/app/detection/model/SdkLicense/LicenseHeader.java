package com.app.detection.model.SdkLicense;

import com.google.gson.annotations.SerializedName;

public class LicenseHeader {

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getPackagename() {
        return packagename;
    }

    public void setPackagename(String packagename) {
        this.packagename = packagename;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getDevice_type() {
        return device_type;
    }

    public void setDevice_type(String device_type) {
        this.device_type = device_type;
    }

    public String getType_of_sdk() {
        return type_of_sdk;
    }

    public void setType_of_sdk(String type_of_sdk) {
        this.type_of_sdk = type_of_sdk;
    }

    @SerializedName("user_id")
    String user_id;

    @SerializedName("unique_id")
    String packagename;

    @SerializedName("device_id")
    String device_id;

    @SerializedName("device_type")
    String device_type;

    @SerializedName("type_of_sdk")
    String type_of_sdk;

}
