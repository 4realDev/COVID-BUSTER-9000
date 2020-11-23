package com.co2team.covidbuster.service;

import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class BackendService {
    private static final String TAG = BackendService.class.getSimpleName();
    private final OkHttpClient client = new OkHttpClient();

    private static final String UPDATE_URL = "https://api.thingspeak.com/update?api_key=7G0T8JBRR8M6OAPD&field";

    public void uploadCo2Measurement(int co2ppm, int roomId) {
        Request request = new Request.Builder()
                .url(UPDATE_URL + roomId + "=" + co2ppm)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                Log.d(TAG, "Uploading successful!");
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d(TAG, "Uploading value to backend failed");
            }
        });
    }
}
