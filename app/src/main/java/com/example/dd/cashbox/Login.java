package com.example.dd.cashbox;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import userhandling.MySingleton;
import userhandling.SessionHandler;

public class Login extends AppCompatActivity {
    private static final String KEY_STATUS = "status";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_FULL_NAME = "full_name";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_EMPTY = "";
    private EditText m_etUsername;
    private EditText m_etPassword;
    private Button m_btnLogin;
    private TextView m_tvRegister;
    private String username;
    private String password;
    private ProgressDialog pDialog;
    private String login_url = "https://www.cashbox-mietkassen.de/api/member/login.php";
    private SessionHandler m_session;
    private View m_decorView;
    private int m_uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        hideSystemUI(getWindow());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //init variables
        m_decorView = getWindow().getDecorView();
        m_session = new SessionHandler(getApplicationContext());
        m_etUsername = findViewById(R.id.activity_login_edt_email);
        m_etPassword = findViewById(R.id.activity_login_edt_pw);
        m_btnLogin = findViewById(R.id.activity_login_btnlogin);
        m_tvRegister = findViewById(R.id.activity_login_tvnoaccouuntsignup);

        //set UI
        m_decorView.setSystemUiVisibility(m_uiOptions);
        m_etUsername.setCursorVisible(false);
        m_etPassword.setCursorVisible(false);

        if (m_session.isLoggedIn()) {
            loadDashboard();
        }

        //listeners
        m_decorView.getViewTreeObserver().addOnGlobalLayoutListener(softkeyboardOnGlobalLayoutListener);
        m_etUsername.setOnEditorActionListener(emailOnEditorActionListener);
        m_etUsername.setOnTouchListener(emailOnTouchListener);
        m_etPassword.setOnEditorActionListener(pwOnEditorActionListener);
        m_etPassword.setOnTouchListener(pwOnTouchListener);
        m_btnLogin.setOnClickListener(loginOnClickListener);
        m_tvRegister.setOnClickListener(registerOnClickListener);
    }

    /////////////////////////// LISTENERS /////////////////////////////////////////////////////////////////////

    private TextView.OnEditorActionListener emailOnEditorActionListener = new TextView.OnEditorActionListener(){
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                m_etUsername.setCursorVisible(false);
            }
            return false;
        }
    };

    private TextView.OnEditorActionListener pwOnEditorActionListener = new TextView.OnEditorActionListener(){
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                m_etPassword.setCursorVisible(false);
            }
            return false;
        }
    };

    private View.OnTouchListener emailOnTouchListener = new View.OnTouchListener(){

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            m_etUsername.setCursorVisible(true);
            return false;
        }
    };

    private View.OnTouchListener pwOnTouchListener = new View.OnTouchListener(){

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            m_etPassword.setCursorVisible(true);
            return false;
        }
    };

    private View.OnClickListener loginOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Retrieve the data entered in the edit texts
            username = m_etUsername.getText().toString().toLowerCase().trim();
            password = m_etPassword.getText().toString().trim();
            if (validateInputs()) {
                login();
            }
        }
    };

    private View.OnClickListener registerOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(Login.this, Register.class);
            startActivity(i);
            finish();
        }
    };

    private ViewTreeObserver.OnGlobalLayoutListener softkeyboardOnGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener(){
        @Override
        public void onGlobalLayout() {
            Rect r = new Rect();
            m_decorView.getWindowVisibleDisplayFrame(r);
            int screenHeight = m_decorView.getRootView().getHeight();

            // r.bottom is the position above soft keypad or device button.
            // if keypad is shown, the r.bottom is smaller than that before.
            int keypadHeight = screenHeight - r.bottom;

            //Log.d(TAG, "keypadHeight = " + keypadHeight);

            if (keypadHeight > screenHeight * 0.5) {
                // keyboard is opened
            }
            else {
                //keyboard is closed
                m_decorView.setSystemUiVisibility(m_uiOptions);
            }
        }
    };

    /////////////////////////// METHODS /////////////////////////////////////////////////////////////////////

    public void hideSystemUI(Window window) {
        m_decorView = window.getDecorView();
        final int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LOW_PROFILE;
        m_decorView.setSystemUiVisibility(uiOptions);
    }

    /**
     * Launch Dashboard Activity on Successful Login
     */
    private void loadDashboard() {
        Intent i = new Intent(getApplicationContext(), Main.class);
        startActivity(i);
        finish();

    }

    /**
     * Display Progress bar while Logging in
     */

    private void displayLoader() {
        pDialog = new ProgressDialog(Login.this);
        pDialog.setMessage("Logging In.. Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

    }

    private void login() {
        displayLoader();
        JSONObject request = new JSONObject();
        try {
            //Populate the request parameters
            request.put(KEY_USERNAME, username);
            request.put(KEY_PASSWORD, password);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsArrayRequest = new JsonObjectRequest
                (Request.Method.POST, login_url, request, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        pDialog.dismiss();
                        try {
                            //Check if user got logged in successfully

                            if (response.getInt(KEY_STATUS) == 0) {
                                m_session.loginUser(username,response.getString(KEY_FULL_NAME));
                                loadDashboard();

                            }else{
                                Toast.makeText(getApplicationContext(),
                                        response.getString(KEY_MESSAGE), Toast.LENGTH_SHORT).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.dismiss();

                        //Display error message whenever an error occurs
                        Toast.makeText(getApplicationContext(),
                                error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this).addToRequestQueue(jsArrayRequest);
    }

    /**
     * Validates inputs and shows error if any
     * @return
     */
    private boolean validateInputs() {
        if(KEY_EMPTY.equals(username)){
            m_etUsername.setError("Username cannot be empty");
            m_etUsername.requestFocus();
            return false;
        }
        if(KEY_EMPTY.equals(password)){
            m_etPassword.setError("Password cannot be empty");
            m_etPassword.requestFocus();
            return false;
        }
        return true;
    }
}