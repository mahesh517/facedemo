package com.app.detection;

/*
 * Copyright 2019 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import java.io.IOException;
import java.util.List;
import com.app.detection.customview.AutoFitTextureView;
import com.app.detection.env.ImageUtils;
import com.app.detection.env.Logger;

import static com.app.detection.CameraConnectionFragment.chooseOptimalSize;

@SuppressLint("ValidFragment")
public class LegacyCameraConnectionFragment extends Fragment {
  private static final Logger LOGGER = new Logger();
  /** Conversion from screen rotation to JPEG orientation. */
  private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
  public SurfaceView surfaceview;
  static {
    ORIENTATIONS.append(Surface.ROTATION_0, 90);
    ORIENTATIONS.append(Surface.ROTATION_90, 0);
    ORIENTATIONS.append(Surface.ROTATION_180, 270);
    ORIENTATIONS.append(Surface.ROTATION_270, 180);
  }
  public RectF static_rectf;
  private Camera camera;
  private Camera.PreviewCallback imageListener;

  private final ConnectionCallback cameraConnectionCallback;
  private Size desiredSize;
  private Size previewSize;
  /** The layout identifier to inflate for this Fragment. */
  private int layout;
  /** An {@link AutoFitTextureView} for camera preview. */
  private AutoFitTextureView textureView;
  /**
   * {@link TextureView.SurfaceTextureListener} handles several lifecycle events on a {@link
   * TextureView}.
   */
  private final TextureView.SurfaceTextureListener surfaceTextureListener =
      new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(
            final SurfaceTexture texture, final int width, final int height) {

          int index = getCameraId();

          camera = Camera.open(index);

          try {
            Camera.Parameters parameters = camera.getParameters();
            List<String> focusModes = parameters.getSupportedFocusModes();
            if (focusModes != null
                && focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
              parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            }
            List<Camera.Size> cameraSizes = parameters.getSupportedPreviewSizes();
            Size[] sizes = new Size[cameraSizes.size()];
            int i = 0;
            for (Camera.Size size : cameraSizes) {
              sizes[i++] = new Size(size.width, size.height);
            }
            previewSize =
                chooseOptimalSize(
                    sizes, desiredSize.getWidth(), desiredSize.getHeight());
            parameters.setPreviewSize(previewSize.getWidth(), previewSize.getHeight());
            camera.setDisplayOrientation(90);
            camera.setParameters(parameters);
            camera.setPreviewTexture(texture);
          } catch (IOException exception) {
            camera.release();
          }

          camera.setPreviewCallbackWithBuffer(imageListener);
          Camera.Size s = camera.getParameters().getPreviewSize();
          camera.addCallbackBuffer(new byte[ImageUtils.getYUVByteSize(s.height, s.width)]);

          textureView.setAspectRatio(s.height, s.width);

          camera.startPreview();
          cameraConnectionCallback.onPreviewSizeChosen(previewSize, 270, static_rectf,width,height);
        }

        @Override
        public void onSurfaceTextureSizeChanged(
            final SurfaceTexture texture, final int width, final int height) {}

        @Override
        public boolean onSurfaceTextureDestroyed(final SurfaceTexture texture) {
          return true;
        }

        @Override
        public void onSurfaceTextureUpdated(final SurfaceTexture texture) {}
      };
  /** An additional thread for running tasks that shouldn't block the UI. */
  private HandlerThread backgroundThread;
  public static LegacyCameraConnectionFragment newInstance(
          final ConnectionCallback callback,
          final Camera.PreviewCallback imageListener,
          final int layout,
          final Size inputSize) {
    return new LegacyCameraConnectionFragment(callback, imageListener, layout, inputSize);
  }
  public LegacyCameraConnectionFragment(final ConnectionCallback connectionCallback,
      final Camera.PreviewCallback imageListener, final int layout, final Size desiredSize) {
    this.cameraConnectionCallback=connectionCallback;
    this.imageListener = imageListener;
    this.layout = layout;
    this.desiredSize = desiredSize;
  }
  public interface ConnectionCallback {
    void onPreviewSizeChosen(Size size, int cameraRotation, RectF rectf, int width, int height);
  }

  @Override
  public View onCreateView(
      final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
    return inflater.inflate(layout, container, false);
  }

  @Override
  public void onViewCreated(final View view, final Bundle savedInstanceState) {
    textureView = (AutoFitTextureView) view.findViewById(R.id.texture);
    surfaceview = (SurfaceView) view.findViewById(R.id.surfaceView);


    SurfaceHolder mHolder = surfaceview.getHolder();
    surfaceview.setZOrderOnTop(true);    // necessary
    SurfaceHolder sfhTrackHolder = surfaceview.getHolder();
    sfhTrackHolder.setFormat(PixelFormat.TRANSPARENT);

    mHolder.addCallback(new SurfaceHolder.Callback() {
      @Override
      public void surfaceCreated(SurfaceHolder surfaceHolder) {

        float left = (surfaceview.getWidth()-surfaceview.getWidth()*0.7f)/2;
        float right = left+surfaceview.getWidth()*0.7f;
        float top = (surfaceview.getHeight()-surfaceview.getHeight()*0.6f)/2;
        float bottom = top+surfaceview.getHeight()*0.6f;
        surfaceview.getHeight();
        Log.e("width",Float.toString(surfaceview.getWidth()*0.7f)+Float.toString(surfaceview.getHeight()*0.6f));
        Canvas canvas = surfaceHolder.lockCanvas();
        static_rectf=new RectF(left,top,right,bottom);
        if (canvas == null) {
          Log.e("TAG", "Cannot draw onto the canvas as it's null");
        } else {
          Paint myPaint = new Paint();
          myPaint.setColor(Color.rgb(100, 20, 50));
          myPaint.setStrokeWidth(10);
          myPaint.setStyle(Paint.Style.STROKE);
          canvas.drawRect(left, top, right, bottom, myPaint);
          surfaceHolder.unlockCanvasAndPost(canvas);
        }

      }

      @Override
      public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

      }

      @Override
      public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

      }
    });
  }

  @Override
  public void onActivityCreated(final Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
  }

  @Override
  public void onResume() {
    super.onResume();
    startBackgroundThread();
    // When the screen is turned off and turned back on, the SurfaceTexture is already
    // available, and "onSurfaceTextureAvailable" will not be called. In that case, we can open
    // a camera and start preview from here (otherwise, we wait until the surface is ready in
    // the SurfaceTextureListener).

    if (textureView.isAvailable()) {
      camera.startPreview();
    } else {
      textureView.setSurfaceTextureListener(surfaceTextureListener);
    }
  }

  @Override
  public void onPause() {
    stopCamera();
    stopBackgroundThread();
    super.onPause();
  }

  /** Starts a background thread and its {@link Handler}. */
  private void startBackgroundThread() {
    backgroundThread = new HandlerThread("CameraBackground");
    backgroundThread.start();
  }

  /** Stops the background thread and its {@link Handler}. */
  private void stopBackgroundThread() {
    backgroundThread.quitSafely();
    try {
      backgroundThread.join();
      backgroundThread = null;
    } catch (final InterruptedException e) {
      LOGGER.e(e, "Exception!");
    }
  }

  protected void stopCamera() {
    if (camera != null) {
      camera.stopPreview();
      camera.setPreviewCallback(null);
      camera.release();
      camera = null;
    }
  }

  private int getCameraId() {
    CameraInfo ci = new CameraInfo();
    for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
      Camera.getCameraInfo(i, ci);
      if (ci.facing == CameraInfo.CAMERA_FACING_FRONT) return i;
    }
    return -1; // No camera found
  }
}
