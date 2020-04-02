package com.app.detection;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class DummyFragment extends Fragment {


    Button retry;
    ClickRetry clickRetry;

    @SuppressLint("ValidFragment")
    public DummyFragment() {

    }


    @Override
    public View onCreateView(
            final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_result, container, false);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {


        retry = view.findViewById(R.id.retry);


        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (clickRetry != null) {
                    clickRetry.onRetry(true);
                }
            }
        });


    }


    public void CallRetryInterfaceMethod(ClickRetry clickRetry) {
        this.clickRetry = clickRetry;
    }


    public interface ClickRetry {
        void onRetry(boolean status);
    }

}
