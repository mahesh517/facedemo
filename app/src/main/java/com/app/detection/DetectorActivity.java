package com.app.detection;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.media.ImageReader.OnImageAvailableListener;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.util.Size;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.View;
import android.widget.Toast;

import com.app.detection.ServiceApi.APIServiceFactory;
import com.app.detection.ServiceApi.ApiService;
import com.app.detection.customview.CustomDialog;
import com.app.detection.customview.OverlayView;
import com.app.detection.customview.OverlayView.DrawCallback;
import com.app.detection.customview.TextSpeech;
import com.app.detection.env.BorderedText;
import com.app.detection.env.ImageUtils;
import com.app.detection.env.Logger;
import com.app.detection.model.SdkLicense.License;
import com.app.detection.model.SdkLicense.LicenseHeader;
import com.app.detection.tflite.Classifier;
import com.app.detection.tflite.TFLiteObjectDetectionAPIModel;
import com.app.detection.tracking.MultiBoxTracker;
import com.app.detection.tracking.Svd;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.TimerTask;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DetectorActivity extends CameraActivity implements OnImageAvailableListener {
    private static final Logger LOGGER = new Logger();

    // Configuration values for the prepackaged SSD model.
    private static final int TF_OD_API_INPUT_SIZE = 300;
    private static final boolean TF_OD_API_IS_QUANTIZED = true;
    private static final String TF_OD_API_MODEL_FILE = "face_mv2_ssd_quant_8bit.tflite";
    private static final String TF_OD_API_LABELS_FILE = "file:///android_asset/labelmap.txt";
    private static final DetectorMode MODE = DetectorMode.TF_OD_API;
    // Minimum detection confidence to track a detection.
    private static final float MINIMUM_CONFIDENCE_TF_OD_API = 0.9f;
    private static final boolean MAINTAIN_ASPECT = false;
    private static final Size DESIRED_PREVIEW_SIZE = new Size(1280, 720);
    private static final boolean SAVE_PREVIEW_BITMAP = true;
    private static final float TEXT_SIZE_DIP = 10;
    OverlayView trackingOverlay;
    private Integer sensorOrientation;


    private Classifier detector;


    boolean eye_blink = false, unitform_status, uniform_process = false, face_detect = false;

    double smileProbabality;

    TimerTask timerTask;

    CountDownTimer countDownTimer, repeatCountDowntimer;


    ApiService apiService, manualAPiService;

    private long lastProcessingTimeMs;
    private Bitmap rgbFrameBitmap = null;
    private Bitmap originialFrameBitmap = null;
    private Bitmap croppedBitmap = null;
    private Bitmap cropCopyBitmap = null;
    private Bitmap uniformBitmap = null;
    private Bitmap facebitmap = null;

    private boolean computingDetection = false;
    private ArrayList<Bitmap> facesBitmap;
    private long timestamp = 0;

    private Matrix frameToCropTransform;
    private Matrix cropToFrameTransform;

    private MultiBoxTracker tracker;

    private RectF boundRect;

    private Matrix frameToDisplayMatrix;
    private int widthofSurfaceView;
    private int heightofSurfaceView;
    CustomDialog alertDialog;
    JSONObject fileObject = null;
    JSONObject resultObject;
    String base64image;
    LoginPrefManager loginPrefManager;
    List<RectF> rectLocations = new ArrayList<>();

    private static final double SMILING_PROB_THRESHOLD = .15;
    private static final double EYE_OPEN_PROB_THRESHOLD = .5;
    private static final double EYE_OPEN_THRESHOLD = 0.8;
    private static final double EYE_CLOSED_THRESHOLD = 0.6;
    private static final double EYE_REOPEN_THRESHOLD = 0.7;

    private int eye_counter = 0;
    private boolean eye_open = false;


    long requestTime = 5000;


    private boolean eye_detection_need = true;
    private boolean uniform_detection_need = true;
    private boolean smile_detection_need = true;
    private boolean final_process = false;


    int lower_h = 170;
    int lower_s = 100;
    int upper_h = 180;
    int upper_s = 255;

    long reuest_time = 5000;

    Bundle input_bundle;


    @Override
    public void onPreviewSizeChosen(final Size size, final int rotation, final RectF rectf, final int width, final int height) {
        final float textSizePx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE_DIP, getResources().getDisplayMetrics());
        BorderedText borderedText = new BorderedText(textSizePx);
        borderedText.setTypeface(Typeface.MONOSPACE);
        widthofSurfaceView = width;
        heightofSurfaceView = height;
        boundRect = rectf;
        apiService = APIServiceFactory.getRetrofit().create(ApiService.class);
        manualAPiService = APIServiceFactory.getRetrofit().create(ApiService.class);
        facesBitmap = new ArrayList<>();

        loginPrefManager = new LoginPrefManager(DetectorActivity.this);
        LicenseHeader licenseHeader = new LicenseHeader();


        Bundle bundle = getIntent().getExtras();
        input_bundle = new Bundle();

        if (bundle != null) {


            if (bundle.containsKey("eye_detection")) {
                eye_detection_need = bundle.getBoolean("eye_detection");
            }
            if (bundle.containsKey("uniform_detection")) {
                uniform_detection_need = bundle.getBoolean("uniform_detection");

                if (!uniform_detection_need) {
                    uniform_process = true;
                }
            }
            if (bundle.containsKey("smile_detection")) {
                smile_detection_need = bundle.getBoolean("smile_detection");
            }
            if (bundle.containsKey("time_out")) {
                requestTime = bundle.getLong("time_out");
            }
            if (bundle.containsKey("lower_H_color_range")) {
                lower_h = bundle.getInt("lower_H_color_range");
            }
            if (bundle.containsKey("upper_H_color_range")) {
                upper_h = bundle.getInt("upper_H_color_range");
            }
            if (bundle.containsKey("lower_S_color_range")) {
                lower_s = bundle.getInt("lower_S_color_range");
            }
            if (bundle.containsKey("upper_S_color_range")) {
                upper_s = bundle.getInt("upper_S_color_range");
            }


        }

        input_bundle.putLong("time_out", requestTime);
        input_bundle.putBoolean("eye_detection", eye_detection_need);
        input_bundle.putBoolean("smile_detection", smile_detection_need);
        input_bundle.putBoolean("eye_detection", eye_detection_need);
        input_bundle.putInt("lower_H_color_range", lower_h);
        input_bundle.putInt("upper_H_color_range", upper_h);
        input_bundle.putInt("lower_S_color_range", lower_s);
        input_bundle.putInt("upper_S_color_range", upper_s);
        tracker = new MultiBoxTracker(this);
        try {
            fileObject = new JSONObject(readJSONFromAsset());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        if (fileObject == null) {
            callBlankFragment();
            showAlert("Please add your admin json file");
            Toast.makeText(this, "Please add your admin json file", Toast.LENGTH_SHORT).show();

        } else {
            Log.e("fileObject", fileObject.toString());
            JSONObject configuration = null;
            try {
                configuration = fileObject.getJSONObject("config");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            if (configuration != null) {

                licenseHeader.setDevice_id(android_id);
                licenseHeader.setDevice_type("android");
                try {
                    licenseHeader.setType_of_sdk(configuration.getString("type_of_sdk"));
                    licenseHeader.setUser_id(configuration.getString("user_id"));
                    licenseHeader.setPackagename(configuration.getString("package_name"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (licenseHeader != null) {
                checkLicense(licenseHeader);
            }
        }
        int cropSize = TF_OD_API_INPUT_SIZE;

        try {
            File file = new File(getExternalFilesDir("Data") + "/face_mv2_ssd_quant_8bit.tflite");
            if (file.exists()) {
            } else {
                unzipUpdateToCache();
            }
//            detector = TFLiteObjectDetectionAPIModel.create(getAssets(), TF_OD_API_MODEL_FILE, TF_OD_API_LABELS_FILE, TF_OD_API_INPUT_SIZE, TF_OD_API_IS_QUANTIZED);
            detector = TFLiteObjectDetectionAPIModel.Newcreate(getAssets(), TF_OD_API_MODEL_FILE, TF_OD_API_LABELS_FILE, TF_OD_API_INPUT_SIZE, TF_OD_API_IS_QUANTIZED, file);
            cropSize = TF_OD_API_INPUT_SIZE;
        } catch (final IOException e) {
            e.printStackTrace();
            LOGGER.e(e, "Exception initializing classifier!");
            Toast toast = Toast.makeText(getApplicationContext(), "Classifier could not be initialized", Toast.LENGTH_SHORT);
            toast.show();
            finish();
        }

        previewWidth = size.getWidth();
        previewHeight = size.getHeight();

        sensorOrientation = 270 - getScreenOrientation();
        LOGGER.i("Camera orientation relative to screen canvas: %d", sensorOrientation);

        LOGGER.i("Initializing at size %dx%d", previewWidth, previewHeight);
        rgbFrameBitmap = Bitmap.createBitmap(previewWidth, previewHeight, Config.ARGB_8888);
        croppedBitmap = Bitmap.createBitmap(cropSize, cropSize, Config.ARGB_8888);

        frameToCropTransform = ImageUtils.getTransformationMatrix(previewWidth, previewHeight, cropSize, cropSize, sensorOrientation, MAINTAIN_ASPECT);

        cropToFrameTransform = new Matrix();
        frameToCropTransform.invert(cropToFrameTransform);

        trackingOverlay = findViewById(R.id.tracking_overlay);

        trackingOverlay.addCallback(
                new DrawCallback() {
                    @Override
                    public void drawCallback(final Canvas canvas) {
                        tracker.draw(canvas);
                        if (isDebug()) {
                            tracker.drawDebug(canvas);
                        }
                    }
                });

        tracker.setFrameConfiguration(previewWidth, previewHeight, sensorOrientation);
    }

    private void checkLicense(final LicenseHeader licenseHeader) {
        apiService.checkLicense(licenseHeader).enqueue(new Callback<License>() {
            @Override
            public void onResponse(Call<License> call, Response<License> response) {

                if (response.raw().code() == 200) {
                    loginPrefManager.setUserToken(licenseHeader.getUser_id());
                } else {
                    setFragment();
                    showAlert("Please use valid json file");
                }
            }

            @Override
            public void onFailure(Call<License> call, Throwable t) {
                showAlert(t.getMessage());
            }
        });
    }

    @Override
    protected void processImage() {


        ++timestamp;
        final long currTimestamp = timestamp;
        trackingOverlay.postInvalidate();

        if (computingDetection) {
            readyForNextImage();
            return;
        }
        computingDetection = true;
        LOGGER.i("Preparing image " + currTimestamp + " for detection in bg thread.");

        rgbFrameBitmap.setPixels(getRgbBytes(), 0, previewWidth, 0, 0, previewWidth, previewHeight);

        originialFrameBitmap = rgbFrameBitmap;

        Matrix matrix1 = new Matrix();
        matrix1.postRotate(270);
        originialFrameBitmap = Bitmap.createBitmap(originialFrameBitmap, 0, 0, rgbFrameBitmap.getWidth(), rgbFrameBitmap.getHeight(), matrix1, true);

        readyForNextImage();

        final Canvas canvas = new Canvas(croppedBitmap);
        canvas.drawBitmap(rgbFrameBitmap, frameToCropTransform, null);


        runInBackground(
                new Runnable() {
                    @Override
                    public void run() {
                        LOGGER.i("Running detection on image " + currTimestamp);

                        Log.e("Running image", "" + currTimestamp);
                        final long startTime = SystemClock.uptimeMillis();

                        final List<Classifier.Recognition> results = detector.recognizeImage(croppedBitmap);
                        lastProcessingTimeMs = SystemClock.uptimeMillis() - startTime;

                        cropCopyBitmap = Bitmap.createBitmap(croppedBitmap);
                        final Canvas canvas = new Canvas(cropCopyBitmap);
                        final Paint paint = new Paint();
                        paint.setColor(Color.RED);
                        paint.setStyle(Style.STROKE);
                        paint.setStrokeWidth(2.0f);


                        float minimumConfidence = MINIMUM_CONFIDENCE_TF_OD_API;
                        switch (MODE) {
                            case TF_OD_API:
                                minimumConfidence = MINIMUM_CONFIDENCE_TF_OD_API;
                                break;
                        }


                        FaceDetector detector1 = new FaceDetector.Builder(DetectorActivity.this)
                                .setTrackingEnabled(false)
                                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                                .build();
                        Frame frame = new Frame.Builder().setBitmap(croppedBitmap).build();
                        SparseArray<Face> faces = detector1.detect(frame);


                        if (smile_detection_need) {

                            if (faces.size() > 0) {
                                Face mFace = faces.valueAt(0);
                                smileProbabality = mFace.getIsSmilingProbability();
                            }
                        }


                        if (faces.size() > 0 && eye_detection_need) {

                            String update = null;
                            for (int i = 0; i < faces.size(); ++i) {
                                Face mFace = faces.valueAt(i);

                                double left_eye_value = mFace.getIsLeftEyeOpenProbability();
                                double right_eye_value = mFace.getIsRightEyeOpenProbability();
                                double avg_eye = (left_eye_value + right_eye_value) / 2;


                                Log.e("avg_eye", "--" + avg_eye);

                                if (avg_eye < EYE_CLOSED_THRESHOLD) {
                                    Log.e("big eye", "eye closed");
                                    eye_blink = true;
                                    eye_counter = 0;

                                }

                            }
                        }


                        final int[] outside = {0};


                        final List<Classifier.Recognition> mappedRecognitions = new LinkedList<Classifier.Recognition>();

                        for (final Classifier.Recognition result : results) {
                            final RectF location = result.getLocation();
                            if (location != null && result.getConfidence() >= minimumConfidence) {


                                location.left = 300 - location.left;
                                location.right = 300 - location.right;
                                cropToFrameTransform.mapRect(location);
                                result.setLocation(location);
                                mappedRecognitions.add(result);


                                int frameWidth = previewWidth;
                                int frameHeight = previewHeight;
                                final boolean rotated = sensorOrientation % 180 == 90;
                                final float multiplier = Math.min((float) heightofSurfaceView / (float) (rotated ? frameWidth : frameHeight), (float) widthofSurfaceView / (float) (rotated ? frameHeight : frameWidth));
                                frameToDisplayMatrix = ImageUtils.getTransformationMatrix(frameWidth, frameHeight, (int) (multiplier * (rotated ? frameHeight : frameWidth)), (int) (multiplier * (rotated ? frameWidth : frameHeight)), sensorOrientation, false);
                                final RectF positionOnDisplay = new RectF(result.getLocation());
                                frameToDisplayMatrix.mapRect(positionOnDisplay);
//
//                                Log.e("maaaaap2", "" + positionOnDisplay);
//                                Log.e("maaaaap1",""+ boundRect);
                                mappedRecognitions.add(result);
                                if (boundRect.left < positionOnDisplay.left && boundRect.right > positionOnDisplay.right && boundRect.top < positionOnDisplay.top && boundRect.bottom > positionOnDisplay.bottom) {
//                                    Log.e("maaaaap3", "" + "Inside");
                                    int areaofBounds = (int) ((boundRect.left - boundRect.right) * (boundRect.top - boundRect.bottom));
                                    int areaofDetected = (int) ((positionOnDisplay.left - positionOnDisplay.right) * (positionOnDisplay.top - positionOnDisplay.bottom));
                                    if (areaofDetected > areaofBounds / 2.5) {
//                                        Log.e("maaaaap", "" + "Just Right");
                                    } else {

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                warning_text.setVisibility(View.VISIBLE);
                                                outside[0] = 0;
                                                warning_text.setText("Please come near to camera");
//                                                Log.e("maaaaap", "" + "Tooooo faaar");
                                            }
                                        });
                                    }
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            warning_text.setVisibility(View.GONE);

                                            outside[0] = 1;
                                        }
                                    });

                                } else {

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                            warning_text.setText("Please come near to bounding box");
                                            warning_text.setVisibility(View.VISIBLE);
                                            outside[0] = 0;
                                        }
                                    });
                                }


                                Bitmap croppedBmp = null;


                                try {
                                    int x1 = (int) location.left - 20;
                                    int y1 = (int) location.top - 20;
                                    int x2 = (int) location.right;
                                    int y2 = (int) location.bottom;
                                    int width = Math.abs(x2 - x1) + 40;
                                    int height = Math.abs(y2 - y1) + 40;
//                                    Log.e("width", width + " " + height);
                                    Matrix matrix = new Matrix();
                                    matrix.postScale(1, -1, rgbFrameBitmap.getWidth() / 2, rgbFrameBitmap.getHeight() / 2);
                                    croppedBmp = Bitmap.createBitmap(rgbFrameBitmap, 0, 0, rgbFrameBitmap.getWidth(), rgbFrameBitmap.getHeight(), matrix, true);
                                    croppedBmp = Bitmap.createBitmap(croppedBmp, x1, y1, width, height);
                                    Matrix matrix1 = new Matrix();
                                    matrix1.postRotate(270);
                                    croppedBmp = Bitmap.createBitmap(croppedBmp, 0, 0, croppedBmp.getWidth(), croppedBmp.getHeight(), matrix1, true);
                                } catch (Exception e) {
                                    Log.e("exce", e.getMessage());
                                }

                                if (mappedRecognitions.size() > 0 && croppedBmp != null) {


                                    if (outside[0] == 1) {
                                        facesBitmap.add(croppedBmp);
                                        rectLocations.add(location);


                                        if (facesBitmap.size() == 1) {
                                            face_detect = true;
                                            initializeTimerTask();
                                        }
                                    }


                                    if (facesBitmap.size() == 5) {

                                        getBestImage();
                                    }
                                }


                            }
                        }


                        tracker.trackResults(mappedRecognitions, currTimestamp);
                        trackingOverlay.postInvalidate();

                        computingDetection = false;

                    }
                });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.camera_connection_fragment_tracking;
    }

    @Override
    protected Size getDesiredPreviewFrameSize() {
        return DESIRED_PREVIEW_SIZE;
    }


    private enum DetectorMode {
        TF_OD_API
    }

    @Override
    protected void setUseNNAPI(final boolean isChecked) {
        runInBackground(new Runnable() {
            @Override
            public void run() {
                detector.setUseNNAPI(isChecked);
            }
        });
    }

    @Override
    protected void setNumThreads(final int numThreads) {
        runInBackground(new Runnable() {
            @Override
            public void run() {
                detector.setNumThreads(numThreads);
            }
        });
    }


    private void getBestImage() {
        List<Double> avg_bitmap = new ArrayList<>();


        for (int k = 0; k < facesBitmap.size(); k++) {
            try {
                Mat src = new Mat();
                Utils.bitmapToMat(facesBitmap.get(k), src);
                Imgproc.cvtColor(src, src, Imgproc.COLOR_BGR2GRAY);
                byte[] return_buff = new byte[(int) (src.total() * src.channels())];
                src.get(0, 0, return_buff);
                Mat resizeimage = new Mat();
                org.opencv.core.Size sz = new org.opencv.core.Size(32, 32);
                Imgproc.resize(src, resizeimage, sz);

                double[][] mat_double = new double[resizeimage.width()][resizeimage.height()];

                for (int i1 = 0; i1 < resizeimage.height(); i1++) {
                    for (int j = 0; j < resizeimage.width(); j++) {
                        String s = Arrays.toString(resizeimage.get(i1, j));
                        s = s.replaceAll("[^\\w\\s]", "");
                        mat_double[i1][j] = Double.parseDouble(s);
                    }
                }

                List<double[]> doubles = Svd.getSvd(mat_double);


                double first_ten = 0;
                double total_sum = 0;
                for (double[] data : doubles) {
                    for (int i2 = 0; i2 < 10; i2++) {
                        first_ten = first_ten + data[i2];
                    }
                    for (int i3 = 0; i3 < data.length; i3++) {
                        total_sum = total_sum + data[i3];
                    }
                }
                avg_bitmap.add(first_ten / total_sum);
            } catch (Exception e) {
            }
        }

        int minIndex = avg_bitmap.indexOf(Collections.min(avg_bitmap));
        Collections.sort(avg_bitmap);

        final Bitmap faceCroped = facesBitmap.get(minIndex);


        RectF rectF = rectLocations.get(minIndex);


        if (rectF != null) {
            try {
                int x1 = (int) rectF.left;
                int y1 = (int) rectF.top;
                int x2 = (int) rectF.right;
                int y2 = (int) rectF.bottom;

                int croped_height = originialFrameBitmap.getHeight() - (y2 + y1);

                int y3 = y2 + y1;
                final Bitmap croppedBmp = Bitmap.createBitmap(originialFrameBitmap, 0, y3 + 80, originialFrameBitmap.getWidth(), croped_height - 80);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (uniform_detection_need) {
                            process(croppedBmp, faceCroped);
                        }
                    }
                });

            } catch (Exception e) {
                Log.e("exce", e.getMessage());
            }

        }


    }


    public String readJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("file.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }


    private MultipartBody.Part prepareFilePart(String imagePth) {
        File file = new File(imagePth);
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        return MultipartBody.Part.createFormData("image", file.getName(), requestFile);
    }


    public void showAlert(String message) {

        alertDialog = new CustomDialog(DetectorActivity.this, R.style.AppTheme, message, new CustomDialog.CloseonCall() {
            @Override
            public void onClick(boolean status) {

                finish();

            }
        });

        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }


    private void unzipUpdateToCache() {
        ZipInputStream zipIs = new ZipInputStream(getResources().openRawResource(R.raw.face_model));
        ZipEntry ze = null;


        try {

            while ((ze = zipIs.getNextEntry()) != null) {
                if (ze.getName().equals("face_mv2_ssd_quant_8bit.tflite")) {
                    FileOutputStream fout = new FileOutputStream(getExternalFilesDir("Data") + "/face_mv2_ssd_quant_8bit.tflite");

                    byte[] buffer = new byte[1024];
                    int length = 0;

                    while ((length = zipIs.read(buffer)) > 0) {
                        fout.write(buffer, 0, length);
                    }
                    zipIs.closeEntry();
                    fout.close();
                }
            }
            zipIs.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    private void process(Bitmap bitmap, Bitmap faceCroped) {
        Mat mat = new Mat();

        uniformBitmap = bitmap;
        facebitmap = faceCroped;
        Bitmap bmp32 = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Utils.bitmapToMat(bmp32, mat);
        Mat normalMat = detectColor(mat);

        if (lower_h == 170 && lower_s == 100 && upper_h == 180 && upper_s == 255) {
            Mat lowerMat = detectLowerColor(mat);

            mattoLowerarry(normalMat, lowerMat);
        } else {
            matToIntArray(normalMat);
        }

    }

    private int[] mattoLowerarry(Mat normalMat, Mat lowerMat) {

        MatOfInt rgb = new MatOfInt(CvType.CV_32S);
        normalMat.convertTo(rgb, CvType.CV_32S);
        int[] rgba = new int[(int) (rgb.total() * rgb.channels())];
        rgb.get(0, 0, rgba);

        MatOfInt lowerrgd = new MatOfInt(CvType.CV_32S);
        lowerMat.convertTo(lowerrgd, CvType.CV_32S);
        int[] lowerrgba = new int[(int) (lowerrgd.total() * lowerrgd.channels())];

        int total_pixle_sum = 0;

        int[] total_array = add(rgba, lowerrgba);

        for (int i = 0; i < total_array.length; i++) {
            total_pixle_sum = total_pixle_sum + rgba[i];
        }

        total_pixle_sum = total_pixle_sum / 255;
        int total_chanels = normalMat.width() * normalMat.height();
        double result = Double.parseDouble(String.valueOf(total_pixle_sum)) / Double.parseDouble(String.valueOf(total_chanels));


        Log.e("result", "--" + result);
        if (result > 0.70) {

            unitform_status = true;
            uniform_process = true;
            final_process = true;
        } else {

            unitform_status = false;
            uniform_process = true;

            final_process = true;
        }


        return rgba;


    }


    public static int[] add(int[] first, int[] second) {
        int length = first.length < second.length ? first.length : second.length;
        int[] result = new int[length];
        for (int i = 0; i < length; i++) {
            result[i] = first[i] + second[i];
            if (result[i] > 255) {
                result[i] = 255;
            }
        }
        return result;
    }


    Mat detectColor(Mat srcImg) {
        final Mat hsvImage = new Mat();
        Mat color_range = new Mat();
        Imgproc.cvtColor(srcImg, hsvImage, Imgproc.COLOR_RGB2HSV);
        Core.inRange(hsvImage, new Scalar(lower_h, lower_s, 0), new Scalar(upper_h, upper_s, 255), color_range);
        return color_range;
    }

    Mat detectLowerColor(Mat srcImg) {
        final Mat hsvImage = new Mat();
        Mat color_range = new Mat();
        Imgproc.cvtColor(srcImg, hsvImage, Imgproc.COLOR_RGB2HSV);
        Core.inRange(hsvImage, new Scalar(0, 100, 0), new Scalar(5, 255, 255), color_range);
        return color_range;
    }


    public int[] matToIntArray(Mat mRgba) {

        MatOfInt rgb = new MatOfInt(CvType.CV_32S);
        mRgba.convertTo(rgb, CvType.CV_32S);
        int[] rgba = new int[(int) (rgb.total() * rgb.channels())];
        rgb.get(0, 0, rgba);
        int total_pixle_sum = 0;

        for (int i = 0; i < rgba.length; i++) {
            total_pixle_sum = total_pixle_sum + rgba[i];
        }
        total_pixle_sum = total_pixle_sum / 255;
        int total_chanels = mRgba.width() * mRgba.height();
        double result = Double.parseDouble(String.valueOf(total_pixle_sum)) / Double.parseDouble(String.valueOf(total_chanels));
        if (result > 0.70) {

            unitform_status = true;
            uniform_process = true;
            final_process = true;
        } else {

            unitform_status = false;
            uniform_process = true;
            final_process = true;
        }


//        if (uniform_process) {
//
//            countDownTimer.cancel();
//
//
//            Intent intent;
//            if (eye_blink) {
//                setOutPut();
//            } else {
//
//                uniform_process = false;
//                unitform_status = false;
//                eye_blink = false;
//                smileProbabality = -1;
//                final_process = false;
//                intent = new Intent(DetectorActivity.this, ResultActivity.class);
//                startActivity(intent);
//            }
//
//            if (countDownTimer != null) {
//                countDownTimer.cancel();
//            }
//            if (repeatCountDowntimer != null) {
//                repeatCountDowntimer.cancel();
//            }
//        }


        return rgba;
    }


    public static int getDominantColor(Bitmap bitmap) {
        Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, 1, 1, true);
        final int color = newBitmap.getPixel(0, 0);
        newBitmap.recycle();
        return color;
    }

    public void initializeTimerTask() {


        Log.e("timer_start", "----");

        countDownTimer = new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {


                long remain = millisUntilFinished / 1000;


                Log.e("timer_start", String.valueOf(remain));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (eye_blink && uniform_process && eye_detection_need) {

                            hideblinkMessage();
                            countDownTimer.cancel();
                            Intent intent;
                            if (eye_blink && eye_detection_need) {
                                setOutPut(1);
                            } else {

                                uniform_process = false;
                                unitform_status = false;
                                eye_blink = false;
                                smileProbabality = -1;

                                callBlankFragment();

//                                intent = new Intent(DetectorActivity.this, ResultActivity.class);
//
//                                intent.putExtras(input_bundle);
//                                startActivity(intent);
//
//                                finish();
                            }


                        } else {


                            if (!eye_detection_need && uniform_process) {

                                if (uniform_detection_need && uniform_process) {
                                    setOutPut(2);
                                } else {
                                    setOutPut(3);
                                }

                            }


                        }


                        if (eye_blink) {

                            hideblinkMessage();
                        } else if (!eye_blink && eye_detection_need) {
                            showBlinkMessage();
                        }

                    }
                });


            }

            @Override
            public void onFinish() {

                Intent intent;

                if (eye_detection_need) {
                    if (eye_blink) {
                        setOutPut(4);
                    } else {

                        uniform_process = false;
                        unitform_status = false;
                        eye_blink = false;
                        smileProbabality = -1;
                        if (final_process) {

                            callBlankFragment();
//                            intent = new Intent(DetectorActivity.this, ResultActivity.class);
//                            intent.putExtras(input_bundle);
//                            startActivity(intent);
//
//                            finish();
                        }
                    }
                } else if (!eye_detection_need) {
                    setOutPut(5);
                }

            }
        };

        countDownTimer.start();
    }


    private void hideblinkMessage() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                eye_tooltip.setText("");
            }
        });
    }

    private void showBlinkMessage() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                eye_tooltip.setText("Please blink eyes");
            }
        });
    }

    private void speechwarning() {
        TextSpeech textSpeech = new TextSpeech(DetectorActivity.this.getApplication());

        textSpeech.play("Please blink eyes");
    }


    private void setOutPut(int position) {


        Log.e("position", "--" + position);

        Intent output = new Intent();

        Bundle bundle = new Bundle();

        int uniform_o_p = 0;
        int smil_o_p = 0;
        int eye_blink_o_p = 0;

        if (unitform_status) {
            uniform_o_p = 1;
        }

        if (smileProbabality < 0.4) {
            smil_o_p = 0;
        } else if (smileProbabality > 0.4 && smileProbabality < 0.6) {
            smil_o_p = 1;
        } else if (smileProbabality > 0.6) {
            smil_o_p = 2;
        }

        if (eye_blink) {
            eye_blink_o_p = 1;
        }

        if (uniform_detection_need) {
            bundle.putInt("shirt_color", uniform_o_p);
        }
        if (smile_detection_need) {
            bundle.putInt("face_mode", smil_o_p);
        }
        if (eye_detection_need) {
            bundle.putInt("eye_blink", eye_blink_o_p);
        }


        bundle.putParcelable("face", cropCopyBitmap);
        output.putExtras(bundle);
        setResult(RESULT_OK, output);


        if (eye_blink) {
            user_image.setImageBitmap(cropCopyBitmap);


        }



        user_name.setTextColor(Color.BLACK);
        user_name.setText(eye_blink_o_p + "/" + uniform_o_p);

        callBlankFragment();

        Handler handler = new Handler();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                eye_blink = false;
                uniform_process = false;

                user_image.setImageBitmap(null);
                user_name.setText("");

                unitform_status = false;
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                }
                setFragment();
            }
        };


        handler.postDelayed(runnable, 3000);

        Log.e("setOutPut", "----");
//        finish();

    }

//
}
