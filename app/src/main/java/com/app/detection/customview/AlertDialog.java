package com.app.detection.customview;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.app.detection.R;

public class AlertDialog extends Dialog {


    String message;
    TextView message_tv;


    public AlertDialog(@NonNull Context context, int themeResId, String message) {
        super(context, themeResId);

        message = this.message;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.alert_dialog);


        message_tv = findViewById(R.id.message);

        message_tv.setText(message);
    }
}
