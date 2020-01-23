package com.app.detection;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Base64;

import androidx.core.content.ContextCompat;

import com.app.detection.ResponseModel.FaceSearchResponse;

import java.io.ByteArrayOutputStream;
import java.io.File;

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


    public FaceSearchResponse getFaceResult(Bitmap bitmap) {

        return detectorActivity.getFaceresultManual(getBase64fromBitmap(bitmap));
    }

    public String getBase64fromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.NO_WRAP);
    }


    public void addImageWithApproval(String filepath, String username) {
        detectorActivity.senWithApproval(filepath, username);
    }


    public void addImageWithOutApproval(File file) {
        detectorActivity.sendDetails(file, 1);
    }

}
