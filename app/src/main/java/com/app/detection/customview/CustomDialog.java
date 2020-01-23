package com.app.detection.customview;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.app.detection.R;

public class CustomDialog extends Dialog {


    String message;
    TextView message_tv, close_btn;

    CloseonCall closeonCall;

    public CustomDialog(Context context, int themeResId, String message, CloseonCall closeonCall) {
        super(context, themeResId);

        this.message = message;
        this.closeonCall = closeonCall;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.alert_dialog);


        message_tv = findViewById(R.id.message);
        close_btn = findViewById(R.id.close_btn);

        message_tv.setText(message);


        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (closeonCall != null) {
                    closeonCall.onClick(true);
                }
            }
        });
    }

    public interface CloseonCall {
        void onClick(boolean status);
    }
}
