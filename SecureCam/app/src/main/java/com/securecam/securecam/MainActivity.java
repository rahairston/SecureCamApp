package com.securecam.securecam;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static String password;
    private boolean isOn;
    private static ArrayList<String> images;
    private static HashMap<String, RecyclerView> folderLayouts;
    private CameraRequest onOffReq;
    private static LinearLayout linear;

    private static TableLayout.LayoutParams tableParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
    private static TableRow.LayoutParams rowParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intentBundle = getIntent();
        password = intentBundle.getStringExtra("password");
        isOn = intentBundle.getBooleanExtra("isOn", false);
        linear = findViewById(R.id.sections);

        final Switch onOff = (Switch) findViewById(R.id.onOffSwitch);
        onOff.setChecked(isOn);
        onOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                HashMap<String, String> data = new HashMap<>();
                data.put("password", password);
                if (isChecked) {
                    // make request to on
                    data.put("switch", "on");
                    onOffReq = new CameraRequest(data);
                } else {
                    // make request to off
                    data.put("switch", "off");
                    onOffReq = new CameraRequest(data);
                }

                onOffReq.execute((Void) null);
            }
        });

        images = new ArrayList<>();
        folderLayouts = new HashMap<>();

        ImagesRequest req = new ImagesRequest(password);
        req.execute((Void) null);
    }

    /**
     * Puts all images from the Images request into the images arraylist
     * @param pictures the list of images
     */
    protected static void populateImages(ArrayList<String> pictures) {
        images.addAll(pictures);
        Set<String> uniqueFolders = new HashSet<>();

        //Image string will be [folder]/[imagename]
        for (String image : images) {
            String beforeSlash = image.split("/")[0];
            uniqueFolders.add(beforeSlash); //use a set for unique strings
        }

        String[] folders = Arrays.copyOf(uniqueFolders.toArray(), uniqueFolders.size(), String[].class);

        for (int i = 0; i < uniqueFolders.size(); i++) {
            String text = folders[i];
            TextView t = new TextView(linear.getContext());
            t.setText(text);
            t.setGravity(Gravity.CENTER_HORIZONTAL);
            t.setTextSize(20);

            linear.addView(t);

            RecyclerView rv = new RecyclerView(linear.getContext());
            rv.setLayoutManager(new GridLayoutManager(linear.getContext(), 3));
            rv.setHasFixedSize(true);
            folderLayouts.put(text, rv);

            linear.addView(rv);
        }

        /* gets all images. Tested and works for individual images

        String test = "7-29-2018/ok.jpg";

        HashMap<String,String> temp = new HashMap<>();
        temp.put("image", test);
        temp.put("password", password);

        ImageRequest req = new ImageRequest(temp);
        req.execute((Void) null);*/
    }

    /**
     * Called upon turning on the camera.
     * Set's up the Session title box and
     * the Session RecycleView
     * We may not use the newFolder due to the fact that we would get double
     * pictures
     */
    protected static void makeSession(String newFolder) {
        TextView t = new TextView(linear.getContext());
        t.setText("Session");
        t.setGravity(Gravity.CENTER_HORIZONTAL);
        t.setTextSize(20);
        t.setId(R.id.sessiontitle);

        //put at index 1 since index 0 is the switch
        linear.addView(t, 1);

        RecyclerView rv = new RecyclerView(linear.getContext());
        rv.setLayoutManager(new GridLayoutManager(linear.getContext(), 3));
        rv.setHasFixedSize(true);
        folderLayouts.put("Session", rv);

        linear.addView(rv, 2);
    }

    /**
     * Called upon turning off the camera.
     * Destroys the Session text and the
     * session RecycleView
     */
    protected static void endSession() {
        linear.removeView(folderLayouts.get("Session"));
        linear.removeView(linear.findViewById(R.id.sessiontitle));
    }

    protected static void setImage(String imagePath, Bitmap image) {
        ImageView i = new ImageView((linear.getContext()));
        i.setImageBitmap(image);
        linear.addView(i);

        //set on click to make fullscreen?
    }
}
