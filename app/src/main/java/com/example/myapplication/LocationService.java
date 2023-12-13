package com.example.myapplication;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;

public interface LocationService {

    @GET("v1/e7bc8693-39b9-4591-b3f2-34779fedf321")
    Call<ResponseBody> locationData();

}
