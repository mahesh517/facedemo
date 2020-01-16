package com.app.detection.model;

import com.google.gson.annotations.SerializedName;

public class SearchHeaderr {

    public String getImage_encoded() {
        return image_encoded;
    }

    public void setImage_encoded(String image_encoded) {
        this.image_encoded = image_encoded;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    @SerializedName("image_encoded")

    String image_encoded;

    @SerializedName("user_id")

    String user_id;


}
