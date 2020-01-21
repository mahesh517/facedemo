package com.app.detection;

import android.content.Context;
import android.content.Intent;

import androidx.core.content.ContextCompat;

import com.app.detection.ResponseModel.FaceSearchResponse;

public class FaceDetection {


    DetectorActivity detectorActivity;


    public FaceDetection() {

    }

    public FaceDetection(DetectorActivity detectorActivity) {
        this.detectorActivity = detectorActivity;
    }


    public void AutomaticDetection(Context context) {

        Intent intent = new Intent(context, DetectorActivity.class);

        ContextCompat.startActivity(context, intent, null);
    }


    public FaceSearchResponse getFaceResult(String base64) {

        return detectorActivity.getFaceresultManual(base64);
    }

}
