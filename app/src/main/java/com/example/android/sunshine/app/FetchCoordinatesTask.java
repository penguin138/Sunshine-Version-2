package com.example.android.sunshine.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by nastya on 29.01.16.
 */
public class FetchCoordinatesTask extends AsyncTask<String,Void,Uri> {
    private Context context;
    public FetchCoordinatesTask(Context context) {
        this.context = context;
    }

    private Uri getCoordinates(String address) throws IOException, JSONException {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("maps.googleapis.com")
                .appendPath("maps")
                .appendPath("api")
                .appendPath("geocode")
                .appendPath("json")
                .appendQueryParameter("address", address);
        String urlString = builder.build().toString();

        URL url = new URL(urlString);

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.connect();

        String response;
        InputStream inputStream = urlConnection.getInputStream();
        StringBuffer buffer = new StringBuffer();
        if (inputStream == null) {
            response = null;
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line + "\n");
        }

        if (buffer.length() == 0) {
            response = null;
        }
        response = buffer.toString();
        JSONObject responseObject = new JSONObject(response);
        String status = responseObject.getString("status");
        double latitude = 37.428434;
        double longitude = -122.0723816;
        if (status.equals("OK")) {
            JSONArray results = responseObject.getJSONArray("results");
            JSONObject geometryObject = results.getJSONObject(0).getJSONObject("geometry");
            JSONObject locationObject = geometryObject.getJSONObject("location");
            latitude = locationObject.getDouble("lat");
            longitude = locationObject.getDouble("lng");
        }
        return Uri.parse("geo:"+latitude +","+longitude+"?z=11");
    }

    @Override
    protected Uri doInBackground(String... params) {
        try {
            return getCoordinates(params[0]);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Uri uri) {
        super.onPostExecute(uri);
        Log.v("lalala", "lalala");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);
        Log.v(uri.toString(),"lalala");
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }
}
