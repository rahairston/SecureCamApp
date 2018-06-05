package com.securecam.securecam;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

public class ImagesRequest extends AsyncTask<Void, Void, Boolean> {

    final String imgURL = LoginActivity.IPANDPORT + "/pictures";

    // This is the data we are sending
    String mPassword;

    //Arraylist of strings
    ArrayList<String> images;

    // This is a constructor that allows you to pass in the JSON body
    public ImagesRequest(String password) {
        mPassword = password;
        images = new ArrayList<>();
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

            urlConnection.setRequestMethod("GET");

            int statusCode = urlConnection.getResponseCode();

            if (statusCode ==  200) {

            InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
            convertInputStreamToString(inputStream); //convert to JSON (look at Graph?)

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
            Log.e("ERROR", "Error requesting Images");
        }
    }

    @Override
    protected void onCancelled() {
    }

    private static void convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        while((line = bufferedReader.readLine()) != null) {
            //result += line;
            Log.e("LINE", line);
        }

        inputStream.close();
    }
}

