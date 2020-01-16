package com.app.detection.ServiceApi;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;


public class APIServiceFactory {

    //    public static final String BASE_URL = "http://192.168.0.6:3000/";
    public static final String BASE_URL = "https://search.facex.io:8443/";
//    public static final String BASE_URL = "http://106.51.133.175:3500/";


    public static Retrofit getRetrofit() {

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder().readTimeout(2, TimeUnit.MINUTES).connectTimeout(2, TimeUnit.MINUTES);

        httpClient.addInterceptor(chain -> {
            Request original = chain.request();

            // Customize the request
            Request request = original.newBuilder()
                    .header("Accept", "application/json")
                    .method(original.method(), original.body())
                    .build();

            Response response = chain.proceed(request);

            return response;
        });

        OkHttpClient OkHttpClient = httpClient.build();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(new NullOnEmptyConverterFactory())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(OkHttpClient)
                .build();

        return retrofit;
    }


}

