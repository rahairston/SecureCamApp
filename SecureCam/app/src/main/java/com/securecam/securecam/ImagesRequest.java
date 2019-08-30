package com.securecam.securecam;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    String mAuthorization;

    //Arraylist of strings
    ArrayList<String> images;

    // This is a constructor that allows you to pass in the JSON body
    public ImagesRequest(String authorization) {
        mAuthorization = authorization;
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
            urlConnection.setRequestProperty("Authorization", mAuthorization);

            urlConnection.setRequestMethod("GET");

            int statusCode = urlConnection.getResponseCode();

            if (statusCode ==  200) {

            InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());

            processReturnInput(inputStream);

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
            MainActivity.populateImages(images);
        } else {
            Log.e("ERROR", "Error requesting Images");
        }
    }

    @Override
    protected void onCancelled() {
    }

    private void processReturnInput(InputStream inputStream) throws IOException, JSONException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null) {
            result += line;
        }

        inputStream.close();

        JSONObject data = new JSONObject(result);

        JSONArray folders = data.getJSONArray("pictures");

        for (int i = 0; i < folders.length(); i++) {
            images.add(folders.getString(i));
        }
    }
}

