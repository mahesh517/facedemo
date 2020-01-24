package com.app.detection;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.app.detection.ServiceApi.APIServiceFactory;
import com.app.detection.ServiceApi.ApiService;
import com.app.detection.customview.ProgressDialog;
import com.app.detection.model.AddUserResponse;
import com.app.detection.model.FaceSearch;
import com.app.detection.model.ManualAddUser;
import com.app.detection.model.SearchHeaderr;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FaceDetection {


    DetectorActivity detectorActivity;
    ApiService apiService;

    ProgressDialog progressDialog;

    ManualSearchResponse searchResponse;


    public FaceDetection() {

        detectorActivity = new DetectorActivity();

        apiService = APIServiceFactory.getRetrofit().create(ApiService.class);

    }


    public void AutomaticDetection(Context context) {

        Intent intent = new Intent(context, DetectorActivity.class);

        ContextCompat.startActivity(context, intent, null);
    }


    public String getBase64fromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.NO_WRAP);
    }


    public void sendDetails(Context context, File file) {
        try {

            showProgressDailog(context);
            RequestBody user_id = RequestBody.create(MediaType.parse("multipart/form-data"), "5d0a8ef72ad9c04228140739");
            apiService.addNewUser(prepareFilePart(file.getAbsolutePath()), user_id).enqueue(new Callback<ManualAddUser>() {
                @Override
                public void onResponse(Call<ManualAddUser> call, Response<ManualAddUser> response) {
                    DropProgressDailog(context);
                    Log.e("sendDetails", new Gson().toJson(response.body()));
                    try {
                        Toast.makeText(context, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Log.e("Exception", e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call<ManualAddUser> call, Throwable t) {
                    try {
                        Toast.makeText(context, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                    }
                }
            });
        } catch (Exception e) {

            Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

        }
    }

    private MultipartBody.Part prepareFilePart(String imagePth) {
        File file = new File(imagePth);
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        return MultipartBody.Part.createFormData("image", file.getName(), requestFile);
    }

    public void senWithApproval(Context context, String imagePath, String username) {
//        try {

        Log.e("path", imagePath + "/" + username);
        showProgressDailog(context);

        RequestBody user_id = RequestBody.create(MediaType.parse("multipart/form-data"), "5d0a8ef72ad9c04228140739");
        RequestBody name = RequestBody.create(MediaType.parse("multipart/form-data"), username);

        apiService.saveNewUser(prepareFilePart(imagePath), name, user_id).enqueue(new Callback<AddUserResponse>() {
            @Override
            public void onResponse(Call<AddUserResponse> call, Response<AddUserResponse> response) {

                Log.e("senWithApproval", new Gson().toJson(response.body()));
                DropProgressDailog(context);
                if (response.raw().code() == 200 && response.body().getStatus().equalsIgnoreCase("ok")) {

                    Toast.makeText(context, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();

//                        startActivity(new Intent(DetectorActivity.this, DetectorActivity.class));
//                        finish();
                } else {
                    Toast.makeText(context, "Fail :" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<AddUserResponse> call, Throwable t) {

                try {

                    DropProgressDailog(context);
                    Toast.makeText(context, "" + t.getMessage(), Toast.LENGTH_SHORT).show();

                } catch (Exception e) {

                }

            }
        });

    }

    public void getFaceresult(Context context, Bitmap faceCroped, ManualSearchResponse manualSearchResponse) {
        try {

            manualSearchResponse = this.searchResponse;
            SearchHeaderr searchHeaderr = new SearchHeaderr();
            searchHeaderr.setImage_encoded(getBase64fromBitmap(faceCroped));
            searchHeaderr.setUser_id("5d0a8ef72ad9c04228140739");

            showProgressDailog(context);
            ManualSearchResponse finalManualSearchResponse = manualSearchResponse;
            apiService.getresult(searchHeaderr).enqueue(new Callback<FaceSearch>() {
                @Override
                public void onResponse(Call<FaceSearch> call, Response<FaceSearch> response) {
                    DropProgressDailog(context);

                    Log.e("response_manual_api", new Gson().toJson(response));

                    FaceSearch faceSearch = response.body();

                    if (finalManualSearchResponse != null) {
                        finalManualSearchResponse.onResponse(faceSearch);
                    }


                }

                @Override
                public void onFailure(Call<FaceSearch> call, Throwable t) {
                    Log.e("onFailure", t.getMessage());

                    DropProgressDailog(context);
                }
            });
        } catch (Exception e) {
            Log.e("Exception", e.getMessage());

            DropProgressDailog(context);
        }
    }

    public interface ManualSearchResponse {
        void onResponse(FaceSearch faceSearch);
    }


    public void showProgressDailog(Context context) {

        progressDialog = new ProgressDialog(context);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

    }

    public void DropProgressDailog(Context context) {
        try {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
        } catch (Exception e) {

        }
    }

}
