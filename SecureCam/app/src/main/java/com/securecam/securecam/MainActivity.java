package com.securecam.securecam;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements ImageFragment.OnFragmentInteractionListener {

    private static String authorization;
    protected static Context context;

    //CameraRequest data
    private boolean isOn;
    private CameraRequest onOffReq;

    //Images/ImageRequest data
    private static ArrayList<String> images;
    private static LinearLayout back;
    private static LinearLayout headers;

    //Snapshot request things
    private static ImageButton snapshot;

    //General data
    protected static ConstraintLayout layout;
    protected static boolean inFragment;

    private static TableLayout.LayoutParams tableParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
    private static TableRow.LayoutParams rowParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;
        layout = (ConstraintLayout)findViewById(R.id.layout);

        Intent intentBundle = getIntent();
        authorization = intentBundle.getStringExtra("authorization");
        isOn = intentBundle.getBooleanExtra("isOn", false);
        back = (LinearLayout)findViewById(R.id.back);
        headers = (LinearLayout)findViewById(R.id.headers);

        snapshot = (ImageButton) findViewById(R.id.snapshot);
        snapshot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snapshot.setImageResource(android.R.drawable.ic_popup_sync);
                RotateAnimation anim = new RotateAnimation(0f, 350f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                anim.setInterpolator(new LinearInterpolator());
                anim.setRepeatCount(Animation.INFINITE);
                anim.setDuration(700);
                snapshot.startAnimation(anim);
                back.setAlpha(0.7f);
                HashMap<String, String> data = new HashMap<>();
                data.put("authorization", authorization);
                SnapshotRequest req = new SnapshotRequest(data);
                req.execute((Void) null);
            }
        });

        final Switch onOff = (Switch) findViewById(R.id.onOffSwitch);
        onOff.setChecked(isOn);
        onOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                HashMap<String, String> data = new HashMap<>();
                data.put("authorization", authorization);
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

        ImagesRequest req = new ImagesRequest(authorization);
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
            TextView t = new TextView(headers.getContext());
            t.setText(text + " >");
            t.setGravity(Gravity.CENTER_HORIZONTAL);
            t.setTextSize(20);

            //Dropdown listener will request images when the text is clicked
            t.setOnClickListener(new DropDownListener(text, images, authorization));

            headers.addView(t);
        }
    }

    /**
     * Called upon turning on the camera.
     * Set's up the Session title box
     * We may not use the newFolder due to the fact that we would get double
     * pictures
     */
    protected static void makeSession(String newFolder) {
        TextView t = new TextView(headers.getContext());
        t.setText("Session");
        t.setGravity(Gravity.CENTER_HORIZONTAL);
        t.setTextSize(20);
        t.setId(R.id.sessiontitle);

        //put at index 1 since index 0 is the switch
        headers.addView(t, 1);
    }

    /**
     * Called upon turning off the camera.
     * Destroys the Session text
     */
    protected static void endSession() {
        headers.removeView(headers.findViewById(R.id.sessiontitle));
    }

    protected static void setImage(String imagePath, final Bitmap image) {
        final String name = imagePath.split("/")[1];
        ImageView i = new ImageView((headers.getContext()));
        i.setImageBitmap(image);
        i.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImage(image, name);
            }
        });
        headers.addView(i);

        //TODO: put linear layout inside of scrolling, and format incoming images
    }

    /**
     * Opens the image in a fragment
     * @param image
     */
    protected static void openImage(Bitmap image, String imageName) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] b = stream.toByteArray();
        inFragment = true;
        snapshot.setAnimation(null);
        snapshot.setImageResource(android.R.drawable.ic_menu_camera);
        back.setAlpha(1.0f);
        FragmentManager fragmentManager = ((Activity)context).getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        ImageFragment fragment = ImageFragment.newInstance(b, imageName);
        fragmentTransaction.add(R.id.layout, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onFragmentInteraction(String uri) {
        System.out.println(uri);
    }

    protected static void returnFromFragment() {
        inFragment = false;
    }

    @Override
    public void onBackPressed() {
        if (inFragment) {
            return;
        }
    }
}
