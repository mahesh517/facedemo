package com.app.detection.customview;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.app.detection.R;

public class NointernetDialog extends Dialog {

    TextView retry, close;
    NointerntInterface nointerntInterface;

    Context context;

    public NointernetDialog(Context context, NointerntInterface nointerntInterface) {
        super(context);
        this.context = context;
        this.nointerntInterface = nointerntInterface;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.no_connection_dialog);

        retry = findViewById(R.id.retry);
        close = findViewById(R.id.close);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nointerntInterface != null){
                    nointerntInterface.onClick(false);
                }
            }
        });
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nointerntInterface != null){
                    nointerntInterface.onClick(true);
                }
            }
        });
    }


    public interface NointerntInterface {
        void onClick(boolean status);
    }
}
