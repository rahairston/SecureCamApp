package com.securecam.securecam;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class CameraRequest extends AsyncTask<Void, Void, Boolean> {

    // This is the data we are sending
    String cameraURL = LoginActivity.IPANDPORT;
    private boolean onorOff;

    JSONObject postData;
    String newFolder;
    String mAuthorization;

    // This is a constructor that allows you to pass in the JSON body
    public CameraRequest(Map<String, String> data) {
        onorOff = (data.get("switch").equals("on")) ? true : false;
        cameraURL += "/camera";
        mAuthorization = data.get("authorization");

        postData = new JSONObject();

        try {
            postData.put("action", data.get("switch"));
        } catch (JSONException e) {
            Log.e("JSON", "Not able to add JSON");
        }
    }


    // This is a function that we are overriding from AsyncTask. It takes Strings as parameters because that is what we defined for the parameters of our async task
    @Override
    protected Boolean doInBackground(Void... params) {

        try {
            // This is getting the url from the string we passed in
            URL url = new URL(cameraURL);

            // Create the urlConnection
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Authorization", mAuthorization);

            urlConnection.setRequestMethod("POST");

            if (this.postData != null) {
                OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
                writer.write(postData.toString());
                writer.flush();
            }

            int statusCode = urlConnection.getResponseCode();

            if (onorOff) {
                InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                processReturnInput(inputStream);
            }

            if (statusCode ==  200) {
                return true;
            } else {
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
            if (onorOff) {
                //turning camera on
                MainActivity.makeSession(newFolder);
            } else {
                //turning camera off
                MainActivity.endSession();
            }
        } else {
            Log.e("ERROR", "Error turning camera on or off: " + cameraURL);
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

        newFolder = data.getString("folder");
    }
}
