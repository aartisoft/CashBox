package com.example.dd.cashbox;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainCash extends AppCompatActivity{

    private Context m_Context;
    private View m_decorView;
    private TextView m_TvToPay;
    private TextView m_TvBack;
    private EditText m_EdtWantsToPay;
    private EditText m_EdtPays;
    private Button m_btnPay;
    private Button m_btnCancel;
    private int m_iSessionTable = -1;
    private int m_iSessionBill = -1;
    private int m_uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        hideSystemUI(getWindow());
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_cash);

        //get intent variables
        m_iSessionTable = getIntent().getIntExtra("TABLE", -1);
        m_iSessionBill = getIntent().getIntExtra("BILL", -1);

        //init variables
        m_Context = this;
        m_decorView = getWindow().getDecorView();
        m_TvToPay = findViewById(R.id.activity_main_cash_pay_tvtopaysum);
        m_TvBack = findViewById(R.id.activity_main_cash_pay_tvrestsum);
        m_EdtWantsToPay = findViewById(R.id.activity_main_cash_pay_edtcustomerwantspaysum);
        m_EdtPays = findViewById(R.id.activity_main_cash_pay_edtcustomerpayssum);
        m_btnPay = findViewById(R.id.activity_main_cash_pay_btnpay);
        m_btnCancel = findViewById(R.id.activity_main_cash_pay_btncancel);

        //set UI
        m_decorView.setSystemUiVisibility(m_uiOptions);
        Toolbar toolbar = findViewById(R.id.activity_main_cash_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //set Listener
        m_decorView.getViewTreeObserver().addOnGlobalLayoutListener(softkeyboardOnGlobalLayoutListener);
        //m_EdtWantsToPay.setOnTouchListener(WantsToPayOnTouchListener);
        //m_EdtPays.setOnTouchListener(PaysOnTouchListener);
    }

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
                //set edittexts
                m_EdtWantsToPay.setCursorVisible(false);
                m_EdtPays.setCursorVisible(false);
                m_decorView.setSystemUiVisibility(m_uiOptions);
            }
        }
    };


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus){
            m_decorView.setSystemUiVisibility(m_uiOptions);
        }
    }

    public View.OnTouchListener WantsToPayOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            m_EdtWantsToPay.setFocusableInTouchMode(true);
            return false;
        }
    };

    public View.OnTouchListener PaysOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            m_EdtPays.setFocusableInTouchMode(true);
            return false;
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(MainCash.this, Main.class);
                intent.putExtra("BILL", m_iSessionBill);
                intent.putExtra("TABLE", m_iSessionTable);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
