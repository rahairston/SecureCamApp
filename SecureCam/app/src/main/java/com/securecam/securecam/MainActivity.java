package com.securecam.securecam;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private String password;
    private boolean isOn;
    private static ArrayList<String> images;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intentBundle = getIntent();
        password = intentBundle.getStringExtra("password");
        isOn = intentBundle.getBooleanExtra("isOn", false);

        images = new ArrayList<>();

        ImagesRequest req = new ImagesRequest(password);
        req.execute((Void) null);
    }

    /**
     * Puts all images from the Images request into the images arraylist
     * @param pictures the list of images
     */
    protected static void populateImages(ArrayList<String> pictures) {
        images.addAll(pictures);
    }
}
