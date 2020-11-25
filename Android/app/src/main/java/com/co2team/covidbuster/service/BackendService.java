package com.co2team.covidbuster.service;

import android.util.Log;

import com.co2team.covidbuster.model.RoomCo2Data;
import com.squareup.moshi.Moshi;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class BackendService {
    private static final String TAG = BackendService.class.getSimpleName();
    private final OkHttpClient client = new OkHttpClient();

    private static final String UPDATE_URL = "https://api.thingspeak.com/update?api_key=7G0T8JBRR8M6OAPD&field";
    private static final String READ_URL = "https://api.thingspeak.com/channels/1224181/fields/";

    ThingsSpeakResponseParseService jsonParser = new ThingsSpeakResponseParseService();

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

    public void readCo2MeasurementsForRoom(int roomId) {
        Request request = new Request.Builder()
                .url(READ_URL + roomId + ".json?average=15&round=0")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful() || responseBody == null) {
                        Log.d(TAG, "Could not read room data! Response was: " + response);
                    } else {
                        // TODO do something with room Data
                        List<RoomCo2Data> roomCo2Data = jsonParser.parseJsonResponse(responseBody.string(), roomId);
                    }
                } catch (JSONException e) {
                    Log.d(TAG, "Error parsing JSON response!");
                    e.printStackTrace();
                }
            }
        });

    }
}
