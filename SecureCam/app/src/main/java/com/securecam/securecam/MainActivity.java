package com.securecam.securecam;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private String password;
    private boolean isOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intentBundle = getIntent();
        password = intentBundle.getStringExtra("password");
        isOn = intentBundle.getBooleanExtra("isOn", false);

        ImagesRequest req = new ImagesRequest(password);
        req.execute((Void) null);
    }
}
