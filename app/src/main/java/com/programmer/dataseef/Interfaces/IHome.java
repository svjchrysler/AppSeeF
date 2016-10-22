package com.programmer.dataseef.Interfaces;

import com.programmer.dataseef.Models.Home;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface IHome {

    @POST("homes")
    Call<ResponseBody> addHome(@Body Home home);
}
