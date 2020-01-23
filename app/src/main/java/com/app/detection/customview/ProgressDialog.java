package com.app.detection.customview;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.app.detection.R;

public class ProgressDialog extends Dialog {


    public ProgressDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.progress_dailog);


    }


    @Override
    public void onBackPressed() {
    }
}
