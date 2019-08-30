package com.securecam.securecam;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * Created by Ryan on 9/5/2018.
 */

public class SnapshotRequest extends AsyncTask<Void, Void, Boolean> {
    final String imgURL = LoginActivity.IPANDPORT + "/snapshot";

    // This is the data we are sending
    String mAuthorization;
    Bitmap image;

    // This is a constructor that allows you to pass in the JSON body
    public SnapshotRequest(Map<String, String> data) {
        mAuthorization = data.get("authorization");
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            // This is getting the url from the string we passed in
            URL url = new URL(imgURL);

            // Create the urlConnection
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setDoInput(true);

            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Authorization", mAuthorization);

            urlConnection.setRequestMethod("GET");

            int statusCode = urlConnection.getResponseCode();

            if (statusCode ==  200) {

                InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                image = BitmapFactory.decodeStream(inputStream);

                return true;
            } else {
                //We'll let the failed Post-Execute handle the false
                return false;
            }

        } catch (Exception e) {
            Log.e("TAG",  e.toString());
            return false;
        }
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        if (success) {
            MainActivity.openImage(image, "snapshot");
        } else {
            Log.e("ERROR", "Error taking snapshot");
        }
    }

    @Override
    protected void onCancelled() {
    }
}
