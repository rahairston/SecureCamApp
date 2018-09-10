package com.securecam.securecam;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by Ryan on 9/9/2018.
 * This class is made as a dropdown handler.
 * When a view is clicked, there will either
 * be data being dropped down or hidden (it is a toggle)
 * Data will only be requested once, then all this will do
 * is hide or show the images
 */

public class DropDownListener implements View.OnClickListener {
    private TextView t;
    private String dropText;
    private ArrayList<String> images;
    private boolean downloaded, dropped;
    private HashMap<String, String> data;
    private ImageRequest req;

    /**
     * Constructor for DropDownListener. We filter all pictures
     * down to just the ones that this dropdown corresponds to
     * as well as setup the ImageRequest data here as well
     * @param text the text of the TextView (without ">" or "v" for substring prevention)
     * @param pictures the names of all pictures to be filtered for this dropdown specifically
     * @param password the password for image request
     */
    DropDownListener(String text, ArrayList<String> pictures, String password) {
        dropText = text;
        downloaded = dropped = false;
        data = new HashMap<>();
        data.put("password", password);
        images = new ArrayList<>();
        for(String p : pictures) {
            if (p.contains(text)) {
                images.add(p);
            }
        }
    }

    @Override
    public void onClick(View v) {
        t = (TextView) v;
        if (!dropped) { //if we are minimized (which we will be first time
            //set text from "abc >" to "abc v"
            t.setText(dropText + " v");
            if(!downloaded) { //first time clicking, we need to download all of them
                for (String i : images) {
                    data.put("image", i); //will replace image value each time.
                    req = new ImageRequest(data);
                    req.execute((Void) null);
                }
            } else {
                //TODO: add list of "views" that will be shown/hidden when clicked
            }
        } else {
            t.setText(dropText + " >");
        }

        //put this at end so after one click downloaded is always true after done once
        if (!downloaded) downloaded = true;
    }
}
