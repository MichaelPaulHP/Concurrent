package com.example.mrrobot.concurrent.Services.ConcurrentService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ConcurrentApiClient {
    private static Retrofit retrofit;

    private static ConcurrentApiService apiService;

    private  ConcurrentApiClient() {

    }

    public static ConcurrentApiService getConcurrentApiService() {

        if(apiService==null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(ConcurrentApiConstants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            apiService= retrofit.create(ConcurrentApiService.class);
        }
        return apiService;
    }
}
