package com.app.detection;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.media.ImageReader;
import android.os.Bundle;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.app.detection.customview.AutoFitTextureView;

public class DummyFragment extends Fragment {


    @SuppressLint("ValidFragment")
    public DummyFragment() {

    }


    @Override
    public View onCreateView(
            final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.blank_screen, container, false);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {


    }

}
