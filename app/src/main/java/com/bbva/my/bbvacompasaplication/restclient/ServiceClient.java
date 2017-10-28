package com.bbva.my.bbvacompasaplication.restclient;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.util.Log;

import com.bbva.my.bbvacompasaplication.model.BbvaModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * @author Sandeep Cherukuri 10/26/17.
 */

public class ServiceClient extends IntentService {

    public static final String PARAM_IN_MSG = "imsg";
    public static final JSONObject PARAM_OUT_MSG = null;
    public static final String PARAM_RECEIVER = "omsg";
    private ArrayList<BbvaModel> bbvaModels;

    public ServiceClient() {
        super("ServiceClient");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        int syncResult = HttpURLConnection.HTTP_BAD_GATEWAY;
        Bundle bundle = new Bundle();
        ResultReceiver receiver = null;

        try {
            receiver = intent.getParcelableExtra(PARAM_RECEIVER);
            getMaps(receiver, syncResult, bundle);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void getMaps(ResultReceiver receiver, int syncResult, Bundle bundle) throws IOException, JSONException {
        HttpURLConnection httpConnection;
        URL url = new URL("https://maps.googleapis.com/maps/api/place/textsearch/json?query=BBVA+Compass&location=MY_LAT,MY_LONG&radius=10000&key=AIzaSyAh35tMCZgkuREVrF8CHFm-X7RQ_q5uN2s");
        URLConnection connection = url.openConnection();
        connection.setConnectTimeout(1000);
        connection.setReadTimeout(1000);

        httpConnection = (HttpURLConnection) connection;
        httpConnection.setUseCaches(false);

        int statusCode = httpConnection.getResponseCode();

        switch (statusCode) {
            case HttpURLConnection.HTTP_OK:
                JSONObject jsonObject = new JSONObject(convertStreamToString(httpConnection.getInputStream()));
                syncResult = statusCode;
                JSONArray jsonArray = jsonObject.getJSONArray("results");
                bbvaModels = new ArrayList<>();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                    JSONObject loc = jsonObject1.getJSONObject("geometry").getJSONObject("location");

                    BbvaModel bbvaModel = BbvaModel
                            .getBuilder()
                            .setAddress(jsonObject1.getString("formatted_address"))
                            .setLat(loc.getDouble("lat"))
                            .setLang(loc.getDouble("lng"))
                            .setName(jsonObject1.getString("name"))
                            .build();

                    bbvaModels.add(bbvaModel);
                }

                bundle.putParcelableArrayList("Model", bbvaModels);
                break;
            case HttpURLConnection.HTTP_FORBIDDEN:
                Log.d("ServiceClient", "HTTP_FORBIDDEN");
                break;
            default:
                Log.d("ServiceClient", "default");
        }

        if (receiver != null) {
            receiver.send(syncResult, bundle);
        }
    }

    public static String convertStreamToString(InputStream is) {
        try {
            return new java.util.Scanner(is).useDelimiter("\\A").next();
        } catch (java.util.NoSuchElementException e) {
            return "";
        }
    }
}
