package com.app.detection;

import android.content.Context;
import android.content.Intent;

import androidx.core.content.ContextCompat;

public class FaceDetection {


    public void AutomaticDetection(Context context) {

        Intent intent = new Intent(context, DetectorActivity.class);

        ContextCompat.startActivity(context, intent, null);
    }

}
