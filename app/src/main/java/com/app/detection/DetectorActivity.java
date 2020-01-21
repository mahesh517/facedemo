

package com.app.detection;

import android.content.Context;
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
import android.os.Build;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Base64;
import android.util.Log;
import android.util.Size;
import android.util.TypedValue;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.app.detection.ResponseModel.FaceSearchResponse;
import com.app.detection.model.AddUserResponse;
import com.app.detection.model.ManualAddUser;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import com.app.detection.ServiceApi.APIServiceFactory;
import com.app.detection.ServiceApi.ApiService;
import com.app.detection.customview.OverlayView;
import com.app.detection.customview.OverlayView.DrawCallback;
import com.app.detection.env.BorderedText;
import com.app.detection.env.ImageUtils;
import com.app.detection.env.Logger;
import com.app.detection.model.FaceSearch;
import com.app.detection.model.SearchHeaderr;
import com.app.detection.tflite.Classifier;
import com.app.detection.tflite.TFLiteObjectDetectionAPIModel;
import com.app.detection.tracking.MultiBoxTracker;
import com.app.detection.tracking.Svd;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

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
    private static final Size DESIRED_PREVIEW_SIZE = new Size(1920, 1080);
    private static final boolean SAVE_PREVIEW_BITMAP = true;
    private static final float TEXT_SIZE_DIP = 10;
    OverlayView trackingOverlay;
    private Integer sensorOrientation;

    private Classifier detector;

    FaceDetection faceDetection;
    BitmapImageAdapter bitmapImageAdapter;

    ApiService apiService, manualAPiService;

    private long lastProcessingTimeMs;
    private Bitmap rgbFrameBitmap = null;
    private Bitmap croppedBitmap = null;
    private Bitmap cropCopyBitmap = null;

    private boolean computingDetection = false;
    private ArrayList<Bitmap> facesBitmap;
    private long timestamp = 0;

    private Matrix frameToCropTransform;
    private Matrix cropToFrameTransform;

    private MultiBoxTracker tracker;

    private RectF boundRect;
    ImagePreviewAdapter imagePreviewAdapter;

    private Matrix frameToDisplayMatrix;
    private int widthofSurfaceView;
    private int heightofSurfaceView;

    JsonObject fileObject;

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

        tracker = new MultiBoxTracker(this);


        fileObject = getJSONResource(DetectorActivity.this);

        faceDetection = new FaceDetection(this);

        if (fileObject == null) {

            callBlankFragment();

            Toast.makeText(this, "Please add your admin json file", Toast.LENGTH_SHORT).show();

        } else {
            Log.e("fileObject", fileObject.toString());
        }
        int cropSize = TF_OD_API_INPUT_SIZE;

        try {
            detector = TFLiteObjectDetectionAPIModel.create(getAssets(), TF_OD_API_MODEL_FILE, TF_OD_API_LABELS_FILE, TF_OD_API_INPUT_SIZE, TF_OD_API_IS_QUANTIZED);
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

        trackingOverlay = (OverlayView) findViewById(R.id.tracking_overlay);

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

    @Override
    protected void processImage() {
        ++timestamp;
        final long currTimestamp = timestamp;
        trackingOverlay.postInvalidate();

        // No mutex needed as this method is not reentrant.
        if (computingDetection) {
            readyForNextImage();
            return;
        }
        computingDetection = true;
        LOGGER.i("Preparing image " + currTimestamp + " for detection in bg thread.");

        rgbFrameBitmap.setPixels(getRgbBytes(), 0, previewWidth, 0, 0, previewWidth, previewHeight);

        readyForNextImage();

        final Canvas canvas = new Canvas(croppedBitmap);
        canvas.drawBitmap(rgbFrameBitmap, frameToCropTransform, null);
        // For examining the actual TF input.
        if (SAVE_PREVIEW_BITMAP) {
//      ImageUtils.saveBitmap(croppedBitmap);
        }

        runInBackground(
                new Runnable() {
                    @Override
                    public void run() {
                        LOGGER.i("Running detection on image " + currTimestamp);
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


                        final int[] outside = {0};

                        final List<Classifier.Recognition> mappedRecognitions = new LinkedList<Classifier.Recognition>();

                        for (final Classifier.Recognition result : results) {
                            final RectF location = result.getLocation();
                            if (location != null && result.getConfidence() >= minimumConfidence) {
//                                canvas.drawCircle( location.bottom, location.left,5, paint);


                                Log.e("face_corrdinates", Float.toString(location.left) + " " + Float.toString(location.right));
                                location.left = 300 - location.left;
                                location.right = 300 - location.right;
                                cropToFrameTransform.mapRect(location);
                                result.setLocation(location);
                                mappedRecognitions.add(result);


                                int frameWidth = (int) previewWidth;
                                int frameHeight = (int) previewHeight;
                                final boolean rotated = sensorOrientation % 180 == 90;
                                final float multiplier =
                                        Math.min(

                                                (float) heightofSurfaceView / (float) (rotated ? frameWidth : frameHeight),
                                                (float) widthofSurfaceView / (float) (rotated ? frameHeight : frameWidth));
                                frameToDisplayMatrix =
                                        ImageUtils.getTransformationMatrix(
                                                frameWidth,
                                                frameHeight,
                                                (int) (multiplier * (rotated ? frameHeight : frameWidth)),
                                                (int) (multiplier * (rotated ? frameWidth : frameHeight)),
                                                sensorOrientation,
                                                false);
                                final RectF positionOnDisplay = new RectF(result.getLocation());
                                frameToDisplayMatrix.mapRect(positionOnDisplay);
//
//                                Log.e("maaaaap2", "" + positionOnDisplay);
//                                Log.e("maaaaap1",""+ boundRect);
                                mappedRecognitions.add(result);
                                if (boundRect.left < positionOnDisplay.left && boundRect.right > positionOnDisplay.right && boundRect.top < positionOnDisplay.top && boundRect.bottom > positionOnDisplay.bottom) {
                                    Log.e("maaaaap3", "" + "Inside");
                                    int areaofBounds = (int) ((boundRect.left - boundRect.right) * (boundRect.top - boundRect.bottom));
                                    int areaofDetected = (int) ((positionOnDisplay.left - positionOnDisplay.right) * (positionOnDisplay.top - positionOnDisplay.bottom));
                                    if (areaofDetected > areaofBounds / 2.5) {
                                        Log.e("maaaaap", "" + "Just Right");
                                    } else {
                                        Log.e("maaaaap", "" + "Tooooo faaar");
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
                                            warning_text.setVisibility(View.VISIBLE);
                                            outside[0] = 0;
                                        }
                                    });
                                    Log.e("maaaaap4", "" + "Outside");
                                }


                                Bitmap croppedBmp = null;


                                try {


                                    int x1 = (int) location.left;
                                    int y1 = (int) location.top;
                                    int x2 = (int) location.right;
                                    int y2 = (int) location.bottom;
                                    int width = Math.abs(x2 - x1);
                                    int height = Math.abs(y2 - y1);
                                    Log.e("width", Integer.toString(width) + " " + Integer.toString(height));
                                    Matrix matrix = new Matrix();
                                    matrix.postScale(1, -1, rgbFrameBitmap.getWidth() / 2, rgbFrameBitmap.getHeight() / 2);
                                    croppedBmp = Bitmap.createBitmap(rgbFrameBitmap, 0, 0, rgbFrameBitmap.getWidth(), rgbFrameBitmap.getHeight(), matrix, true);
                                    croppedBmp = Bitmap.createBitmap(croppedBmp, x1, y1, width, height);
                                    Matrix matrix1 = new Matrix();
                                    matrix1.postRotate(270);
//
                                    croppedBmp = Bitmap.createBitmap(croppedBmp, 0, 0, croppedBmp.getWidth(), croppedBmp.getHeight(), matrix1, true);

                                    //


                                } catch (Exception e) {


                                    Log.e("exce", e.getMessage());
                                }

                                if (mappedRecognitions.size() > 0 && croppedBmp != null) {


                                    if (outside[0] == 1) {
                                        facesBitmap.add(croppedBmp);
                                    }


                                    bitmapImageAdapter = new BitmapImageAdapter(DetectorActivity.this, facesBitmap);

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            recyclerView.setAdapter(bitmapImageAdapter);
                                        }
                                    });

                                    if (facesBitmap.size() == 10) {

//                                        setFragment();

                                        callBlankFragment();
                                        getBestImage();
                                    }
                                }


                            }
                        }


                        tracker.trackResults(mappedRecognitions, currTimestamp);
                        trackingOverlay.postInvalidate();

                        computingDetection = false;

                        runOnUiThread(
                                new Runnable() {
                                    @Override
                                    public void run() {
//                                        showFrameInfo(previewWidth + "x" + previewHeight);
//                                        showCropInfo(cropCopyBitmap.getWidth() + "x" + cropCopyBitmap.getHeight());
//                                        showInference(lastProcessingTimeMs + "ms");


                                    }
                                });
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

    // Which detection model to use: by default uses Tensorflow Object Detection API frozen
    // checkpoints.
    private enum DetectorMode {
        TF_OD_API;
    }

    @Override
    protected void setUseNNAPI(final boolean isChecked) {
        runInBackground(() -> detector.setUseNNAPI(isChecked));
    }

    @Override
    protected void setNumThreads(final int numThreads) {
        runInBackground(() -> detector.setNumThreads(numThreads));
    }


    private void getBestImage() {
        List<Double> avg_bitmap = new ArrayList<>();

        Log.e("facesBitmap", "" + facesBitmap.size());

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
                Log.e("avg", "--" + first_ten / total_sum);
            } catch (Exception e) {
                Log.e("best_exception", e.getMessage());
            }
        }

        int minIndex = avg_bitmap.indexOf(Collections.min(avg_bitmap));
        Collections.sort(avg_bitmap);
        Log.e("final_bestavg", "--" + avg_bitmap.get(0));
        Log.e("avgminindex", "--" + minIndex);

        Bitmap faceCroped = facesBitmap.get(minIndex);
        if (faceCroped != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    getFaceresult(getBase64String(faceCroped), faceCroped);
                }
            });
        }
    }


    private String getBase64String(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.NO_WRAP);
    }


    public void getFaceresult(String base64String, Bitmap faceCroped) {
        try {

            SearchHeaderr searchHeaderr = new SearchHeaderr();
            searchHeaderr.setImage_encoded(base64String);
            searchHeaderr.setUser_id("5d0a8ef72ad9c04228140739");


            generateNoteOnSD(DetectorActivity.this, System.currentTimeMillis() + ".txt", base64String);
            apiService.getresult(searchHeaderr).enqueue(new Callback<FaceSearch>() {
                @Override
                public void onResponse(Call<FaceSearch> call, Response<FaceSearch> response) {
                    if (response.raw().code() == 200 && response.body().getStatus().equalsIgnoreCase("ok")) {

                        List<com.app.detection.model.User> users = response.body().getData().getUser();
                        if (users.size() > 0) {
                            String id = null;
                            for (com.app.detection.model.User user : users) {
                                id = user.getId();
                                Log.e("user", new Gson().toJson(user));
                                String jsonResponse = new Gson().toJson(user);
                                JSONObject jsonObject = null;
                                try {
                                    jsonObject = new JSONObject(jsonResponse);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                Calendar calendar = Calendar.getInstance();
                                String image_url = APIServiceFactory.BASE_URL + user.getFileDirectory();

                                facesBitmap.clear();


                                List<String> imageUrl = new ArrayList<>();

                                imageUrl.add(image_url);

                                imagePreviewAdapter = new ImagePreviewAdapter(DetectorActivity.this, imageUrl, new ImagePreviewAdapter.ViewHolder.OnItemClickListener() {
                                    @Override
                                    public void onClick(View v, int position) {

                                    }
                                });


                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        recyclerView.setAdapter(imagePreviewAdapter);
                                    }
                                });
                                if (jsonObject.has("person_name")) {


                                    break;
                                }
                            }

                            setFragment();
                        } else {
                            Toast.makeText(DetectorActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {


                        sendDetails(createfile(faceCroped));
                        Toast.makeText(DetectorActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();


                    }
                }

                @Override
                public void onFailure(Call<FaceSearch> call, Throwable t) {
                    Log.e("onFailure", t.getMessage());
                }
            });
        } catch (Exception e) {
            Log.e("Exception", e.getMessage());
        }
    }


    public FaceSearchResponse getFaceresultManual(String base64String) {


        FaceSearchResponse faceSearch = new FaceSearchResponse();
        try {

            SearchHeaderr searchHeaderr = new SearchHeaderr();
            searchHeaderr.setImage_encoded(base64String);
            searchHeaderr.setUser_id("5d0a8ef72ad9c04228140739");


//            generateNoteOnSD(DetectorActivity.this, System.currentTimeMillis() + ".txt", base64String);
            manualAPiService.getresult(searchHeaderr).enqueue(new Callback<FaceSearch>() {
                @Override
                public void onResponse(Call<FaceSearch> call, Response<FaceSearch> response) {


                    faceSearch.setResponseCode(response.raw().code());
                    faceSearch.setData(response.body().getData());
                    faceSearch.setMeesage(response.body().getMessage());
                    if (response.raw().code() == 200 && response.body().getStatus().equalsIgnoreCase("ok")) {

                        List<com.app.detection.model.User> users = response.body().getData().getUser();
                        if (users.size() > 0) {
                            String id = null;
                            for (com.app.detection.model.User user : users) {
                                id = user.getId();
                                Log.e("user", new Gson().toJson(user));
                                String jsonResponse = new Gson().toJson(user);
                                JSONObject jsonObject = null;
                                try {
                                    jsonObject = new JSONObject(jsonResponse);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                Calendar calendar = Calendar.getInstance();
                                String image_url = APIServiceFactory.BASE_URL + user.getFileDirectory();

                                facesBitmap.clear();


                                List<String> imageUrl = new ArrayList<>();

                                imageUrl.add(image_url);

                                imagePreviewAdapter = new ImagePreviewAdapter(DetectorActivity.this, imageUrl, new ImagePreviewAdapter.ViewHolder.OnItemClickListener() {
                                    @Override
                                    public void onClick(View v, int position) {

                                    }
                                });


                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        recyclerView.setAdapter(imagePreviewAdapter);
                                    }
                                });
                                if (jsonObject.has("person_name")) {


                                    break;
                                }
                            }

                            setFragment();
                        } else {
                            Toast.makeText(DetectorActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {

                        Toast.makeText(DetectorActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();

                        setFragment();

                    }


                }


                @Override
                public void onFailure(Call<FaceSearch> call, Throwable t) {
                    Log.e("onFailure", t.getMessage());

                    faceSearch.setResponseCode(401);
                    faceSearch.setData(null);
                    faceSearch.setMeesage("onFailure" + t.getLocalizedMessage());
                }
            });
        } catch (Exception e) {

            faceSearch.setResponseCode(101);
            faceSearch.setData(null);
            faceSearch.setMeesage(e.getLocalizedMessage());
            Log.e("Exception", e.getMessage());
        }
        return faceSearch;
    }


    public void generateNoteOnSD(Context context, String sFileName, String sBody) {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "Notes");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, sFileName);
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(sBody);
            writer.flush();
            writer.close();
            Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public JsonObject getJSONResource(Context context) {
        try (InputStream is = context.getAssets().open("file.json")) {
            JsonParser parser = new JsonParser();
            return parser.parse(new InputStreamReader(is)).getAsJsonObject();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);

        }
        return null;
    }

    private File createfile(Bitmap bitmp) {
        File f = new File(getCacheDir(), System.currentTimeMillis() + ".jpg");
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bitmap bitmap = bitmp;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100 /*ignored for PNG*/, bos);
        byte[] bitmapdata = bos.toByteArray();

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return f;
    }


    private MultipartBody.Part prepareFilePart(String imagePth) {
        File file = new File(imagePth);
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        return MultipartBody.Part.createFormData("image", file.getName(), requestFile);
    }


    private void sendDetails(File file) {
        try {


            RequestBody user_id = RequestBody.create(MediaType.parse("multipart/form-data"), "5d0a8ef72ad9c04228140739");
            apiService.addNewUser(prepareFilePart(file.getAbsolutePath()), user_id).enqueue(new Callback<ManualAddUser>() {
                @Override
                public void onResponse(Call<ManualAddUser> call, Response<ManualAddUser> response) {
                    try {
                        setFragment();
                    } catch (Exception e) {
                        Log.e("Exception", e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call<ManualAddUser> call, Throwable t) {
                    try {

                        setFragment();
                    } catch (Exception e) {
                    }
                }
            });
        } catch (Exception e) {
        }
    }


    public void sendDetails(String imagePath, String username) {
        try {

            RequestBody user_id = RequestBody.create(MediaType.parse("multipart/form-data"), "5d0a8ef72ad9c04228140739");
            RequestBody name = RequestBody.create(MediaType.parse("multipart/form-data"), username);


            apiService.saveNewUser(prepareFilePart(imagePath), name, user_id).enqueue(new Callback<AddUserResponse>() {
                @Override
                public void onResponse(Call<AddUserResponse> call, Response<AddUserResponse> response) {


                    Log.e("path", imagePath);
                    Log.e("response", new Gson().toJson(response.body()));
                    Log.e("code", new Gson().toJson(response.raw().code()));

                    if (response.raw().code() == 200 && response.body().getStatus().equalsIgnoreCase("ok")) {

                        Toast.makeText(DetectorActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(DetectorActivity.this, DetectorActivity.class));
                        finish();
                    } else {
                        Toast.makeText(DetectorActivity.this, "Fail :" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(Call<AddUserResponse> call, Throwable t) {

                    try {
                        Toast.makeText(DetectorActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();

                    } catch (Exception e) {

                    }

                }
            });

        } catch (Exception e) {
        }
    }


}
