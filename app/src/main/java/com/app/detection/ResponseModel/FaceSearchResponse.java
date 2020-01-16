package com.app.detection.ResponseModel;

import com.app.detection.model.Data;

public class FaceSearchResponse {

    public String getMeesage() {
        return meesage;
    }

    public void setMeesage(String meesage) {
        this.meesage = meesage;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String meesage;
    public Data data;

    public int responseCode;

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }
}
