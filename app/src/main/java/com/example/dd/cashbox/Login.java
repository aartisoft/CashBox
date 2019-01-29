package com.example.dd.cashbox;

import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;


public class Login extends AppCompatActivity implements OnClickListener {

    private View m_decorView;
    private EditText m_tbLicenceKey;
    private Button m_btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //init variables
        m_tbLicenceKey = (EditText) findViewById(R.id.tbLicenceKey);
        m_btnLogin = findViewById(R.id.btnLogin);
        m_btnLogin.setEnabled(false);

        //hide header and footer
        m_decorView = getWindow().getDecorView();
        final int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        m_decorView.setSystemUiVisibility(uiOptions);

        //SoftKeyBoardListener
        m_decorView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                m_decorView.getWindowVisibleDisplayFrame(r);
                int screenHeight = m_decorView.getRootView().getHeight();

                // r.bottom is the position above soft keypad or device button.
                // if keypad is shown, the r.bottom is smaller than that before.
                int keypadHeight = screenHeight - r.bottom;

                //Log.d(TAG, "keypadHeight = " + keypadHeight);

                if (keypadHeight > screenHeight * 0.10) {
                    // keyboard is opened
                }
                else {
                    //keyboard is closed
                    m_tbLicenceKey.setCursorVisible(false);
                    m_decorView.setSystemUiVisibility(uiOptions);
                }
            }
        });

        //init events
        m_btnLogin.setOnClickListener(this);
        m_tbLicenceKey.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(!m_tbLicenceKey.getText().equals("")){
                    m_btnLogin.setEnabled(true);
                }
                else{
                    m_btnLogin.setEnabled(false);
                }
            }
        });
    }

    @Override
    public void onClick(View v){
        startActivity(new Intent(this, Main.class));
    }
}
