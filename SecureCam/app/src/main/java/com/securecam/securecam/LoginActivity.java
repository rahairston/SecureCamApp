package com.securecam.securecam;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    protected static final String IPANDPORT = "http://10.10.90.234:3000";

    private String loginUrl = IPANDPORT + "/login";

    Handler handler;

    private final int ERROR = 1;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;
    private String message;

    // UI references.
    private EditText mPasswordView;
    private EditText mUsernameView;
    private TextView mErrorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.

        mUsernameView = (EditText) findViewById(R.id.username);
        mPasswordView = (EditText) findViewById(R.id.password);
        mErrorView = findViewById(R.id.error);
//        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
//                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
//                    attemptLogin();
//                    return true;
//                }
//                return false;
//            }
//        });

        handler = new Handler() {
            public void handleMessage(Message msg) {
                final int what = msg.what;
                switch(what) {
                    case ERROR: updateError(); break;
                }
            }
        };

        mErrorView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                ((TextView) view).setText("");
            }
        });

        Button mSignInButton = (Button) findViewById(R.id.loginButton);
        mSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String username = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError("The username cannot be empty");
            focusView = mUsernameView;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError("The password cannot be empty");
            focusView = mPasswordView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            mAuthTask = new UserLoginTask(username, password);
            mAuthTask.execute((Void) null);
        }
    }

    public void updateError() {
        mErrorView.setText(message);
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private String authorization;
        private boolean isOn = false;

        UserLoginTask(String username, String password) {
            StringBuilder sb = new StringBuilder();
            sb.append(username)
                .append(":")
                .append(password);
            try {
                authorization = "Basic " + Base64.encodeToString(sb.toString().getBytes("utf-8"), Base64.DEFAULT);
            } catch (UnsupportedEncodingException e) {authorization = "";}
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                // This is getting the url from the string we passed in
                URL url = new URL(loginUrl);

                // Create the urlConnection
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setDoInput(true);

                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Authorization", authorization);

                urlConnection.setRequestMethod("GET");

                int statusCode = urlConnection.getResponseCode();

                if (statusCode ==  200) {
                    InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                    isOn = convertInputStreamToString(inputStream).equals("true");
                    return true;
                } else {
                    //We'll let the failed Post-Execute handle the false
                    switch (statusCode) {
                        case 401:
                            message = "Username or password may be incorrect";
                            break;
                        default:
                            message = statusCode + " " + urlConnection.getResponseMessage();
                    }

                    return false;
                }

            } catch (Exception e) {
                Log.e("TAG", e.getMessage());
                message = e.getMessage();
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;

            if (success) {
                Intent intentBundle = new Intent(LoginActivity.this, MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("authorization", authorization);
                bundle.putBoolean("isOn", isOn);
                intentBundle.putExtras(bundle);
                startActivity(intentBundle);
            } else {
                handler.sendEmptyMessage(ERROR);
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }
}

