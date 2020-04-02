package com.app.detection;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.app.detection.ServiceApi.ApiService;
import com.app.detection.customview.ProgressDialog;

import org.json.JSONObject;

public class FaceDetection {


    DetectorActivity detectorActivity;
    ApiService apiService;

    ProgressDialog progressDialog;

    LoginPrefManager loginPrefManager;
    Context context;
    JSONObject fileObject = null;

    public int AUTOMATIC_REQUEST_CODE = 123;

    boolean eye_detection = false;
    boolean uniform_detection = false;
    boolean smile_detection = false;
    long request_time = 5000;

    public FaceDetection(Context context, Activity activity, long requestTime, boolean eye_detection, boolean uniform_detection, boolean smile_detection, int lower_H_color_range, int upper_H_color_range, int lower_S_color_range, int upper_S_color_range) {

        this.context = context;
        Intent intent = new Intent(context, DetectorActivity.class);

        Bundle bundle = new Bundle();

        bundle.putBoolean("eye_detection", eye_detection);
        bundle.putBoolean("uniform_detection", uniform_detection);
        bundle.putBoolean("smile_detection", smile_detection);
        bundle.putLong("time_out", request_time);
        bundle.putInt("lower_H_color_range", lower_H_color_range);
        bundle.putInt("upper_H_color_range", upper_H_color_range);
        bundle.putInt("lower_S_color_range", lower_S_color_range);
        bundle.putInt("upper_S_color_range", upper_S_color_range);
        intent.putExtras(bundle);
        activity.startActivityForResult(intent, AUTOMATIC_REQUEST_CODE);

    }


    public FaceDetection(Context context, Activity activity) {
        Intent intent = new Intent(context, DetectorActivity.class);


        activity.startActivityForResult(intent, AUTOMATIC_REQUEST_CODE);
    }


}
