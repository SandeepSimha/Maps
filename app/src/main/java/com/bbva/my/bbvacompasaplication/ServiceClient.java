package com.bbva.my.bbvacompasaplication;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

/**
 * Created by chers026 on 10/26/17.
 */

public class ServiceClient extends IntentService {

    public static final String PARAM_IN_MSG = "imsg";
    public static final JSONObject PARAM_OUT_MSG = null;
    public static final String PARAM_RECEIVER = "omsg";

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
            getFile(receiver, syncResult, bundle);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void getFile(ResultReceiver receiver, int syncResult, Bundle bundle) throws IOException, JSONException {
        HttpURLConnection httpConnection;
        URL url = new URL("https://maps.googleapis.com/maps/api/place/textsearch/json?query=BBVA+Compass&location=MY_LAT,MY_LONG&radius=10000&key=AIzaSyC0uPrTX4-wzXzRzThNrYYHhOO7C0GQTVc");
        //https://maps.googleapis.com/maps/api/place/textsearch/json?query=BBVA+Compass&location=MY_LAT,MY_LONG&radius=10000&key=AIzaSyC0uPrTX4-wzXzRzThNrYYHhOO7C0GQTVc

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
                Log.d("ServiceClient", "jsonObject" + jsonObject);
                bundle.putString("token", jsonObject.getString("next_page_token"));

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

    public static Intent createIntent(Context context, GenericReceiver receiver) {
        Intent intent = new Intent(Intent.ACTION_SYNC, null, context, ServiceClient.class);
        intent.putExtra(PARAM_RECEIVER, receiver);
        return intent;
    }
}
