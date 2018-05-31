package com.securecam.securecam;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class ImageRequest extends AsyncTask<Void, Void, Boolean> {

    final String imgURL = LoginActivity.IPANDPORT + "/picture";

    // This is the data we are sending
    String img;
    String mPassword;

    // This is a constructor that allows you to pass in the JSON body
    public ImageRequest(Map<String, String> data) {
        img = data.get("image");
        mPassword = data.get("password");
    }


    // This is a function that we are overriding from AsyncTask. It takes Strings as parameters because that is what we defined for the parameters of our async task
    @Override
    protected Boolean doInBackground(Void... params) {

        try {
            // This is getting the url from the string we passed in
            URL url = new URL(imgURL);

            // Create the urlConnection
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setDoInput(true);

            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("password", mPassword);
            urlConnection.setRequestProperty("picture", img);

            urlConnection.setRequestMethod("GET");

            int statusCode = urlConnection.getResponseCode();

            if (statusCode ==  200) {

                InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());

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
            //MainActivity.setRiskArrays(riskTimes, pushDuration, liftDuration, pushFrequency, liftFrequency);
        } else {
            Log.e("ERROR", "Error requesting Image: " + img);
        }
    }

    @Override
    protected void onCancelled() {
    }
}
