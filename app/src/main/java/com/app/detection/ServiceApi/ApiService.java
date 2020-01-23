package com.app.detection.ServiceApi;


import com.app.detection.model.AddUserResponse;
import com.app.detection.model.FaceSearch;
import com.app.detection.model.LoginApi.Login;
import com.app.detection.model.LoginHeader;
import com.app.detection.model.ManualAddUser;
import com.app.detection.model.SdkLicense.License;
import com.app.detection.model.SdkLicense.LicenseHeader;
import com.app.detection.model.SearchHeaderr;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {


    @POST("auth/searchWithEncodedImage")
    Call<FaceSearch> getresult(@Body SearchHeaderr searchHeader);


    @Multipart
    @POST("images/singleImage/")
    Call<AddUserResponse> saveNewUser(@Part MultipartBody.Part file,
                                      @Part("name") RequestBody name,
                                      @Part("user_id") RequestBody user_id);


    @Multipart
    @POST("images/singleImageForApproval")
    Call<ManualAddUser> addNewUser(@Part MultipartBody.Part file,
                                   @Part("user_id") RequestBody userId);


    @POST("sdk/checkTheUserHaveValidLicenseToInstallSdk")
    Call<License> checkLicense(@Body LicenseHeader licenseHeader);
}
