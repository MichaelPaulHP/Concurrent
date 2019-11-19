package com.example.mrrobot.concurrent.Services.ConcurrentService;



import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ConcurrentApiService {

    //@Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST(ConcurrentApiConstants.REGISTER_USER)
    Call<String> registerUser(@Field("userId") String userId,
                              @Field("userName") String userName,
                              @Field("email") String email,
                              @Field("password") String password);
}
