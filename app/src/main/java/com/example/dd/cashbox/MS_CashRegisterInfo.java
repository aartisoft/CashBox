package com.example.dd.cashbox;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import SQLite.SQLiteDatabaseHandler_Category;
import SQLite.SQLiteDatabaseHandler_Product;
import SQLite.SQLiteDatabaseHandler_Settings;
import fragments.ChooseColorDialogFragment;
import global.GlobVar;
import objects.ObjCategory;
import objects.ObjPrinter;
import objects.ObjProduct;

public class MS_CashRegisterInfo extends AppCompatActivity {

    private FloatingActionButton m_fab;
    private Context m_Context;
    private EditText m_EditTextHostName;
    private EditText m_EditTextPartyName;
    private View m_decorView;
    private boolean m_bChanged = false;
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
        setContentView(R.layout.activity_ms_cashregisterinfo);

        //init variables
        m_Context = this;
        m_EditTextHostName = findViewById(R.id.ms_cashregister_tvhostname);
        m_EditTextPartyName = findViewById(R.id.ms_cashregister_tvpartyname);
        m_fab = findViewById(R.id.ms_cashregister_fab);
        m_decorView = getWindow().getDecorView();


        //set UI
        m_decorView.setSystemUiVisibility(m_uiOptions);
        Toolbar toolbar = findViewById(R.id.ms_cashregister_tb);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        m_fab.setEnabled(false);
        m_fab.setAlpha(120);

        //set values
        getData();

        //set Listener
        m_fab.setOnClickListener(fabOnClickListener);
        m_decorView.getViewTreeObserver().addOnGlobalLayoutListener(softkeyboardOnGlobalLayoutListener);
        m_EditTextHostName.setOnEditorActionListener(DoneOnEditorHostNameActionListener);
        m_EditTextHostName.setOnTouchListener(nameOnTouchHostNameListener);
        m_EditTextPartyName.setOnEditorActionListener(DoneOnEditorPartyNameActionListener);
        m_EditTextPartyName.setOnTouchListener(nameOnTouchPartyNameListener);
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
                m_decorView.setSystemUiVisibility(m_uiOptions);
            }
        }
    };

    private OnClickListener fabOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                setData();
                m_fab.setEnabled(false);
                m_fab.setAlpha(120);
                Toast.makeText(MS_CashRegisterInfo.this, getResources().getString(R.string.src_AenderungenWurdenGespeichert), Toast.LENGTH_SHORT).show();
        }
    };

    private OnEditorActionListener DoneOnEditorHostNameActionListener = new OnEditorActionListener(){
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                m_EditTextHostName.setCursorVisible(false);
                dataChanged();
            }
            return false;
        }
    };

    private View.OnTouchListener nameOnTouchHostNameListener = new View.OnTouchListener(){

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            m_EditTextHostName.setCursorVisible(true);
            dataChanged();
            return false;
        }
    };

    private OnEditorActionListener DoneOnEditorPartyNameActionListener = new OnEditorActionListener(){
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                m_EditTextPartyName.setCursorVisible(false);
                dataChanged();
            }
            return false;
        }
    };

    private View.OnTouchListener nameOnTouchPartyNameListener = new View.OnTouchListener(){

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            m_EditTextPartyName.setCursorVisible(true);
            dataChanged();
            return false;
        }
    };


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus){
            m_decorView.setSystemUiVisibility(m_uiOptions);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(MS_CashRegisterInfo.this, MenuSettings.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    ///////////////////////////////////////// METHODS //////////////////////////////////////////////////////////////////////
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

   private void setData(){
        if(!m_EditTextHostName.getText().toString().equals("")){
            GlobVar.g_ObjSession.setHostName(m_EditTextHostName.getText().toString());
            GlobVar.g_ObjSession.setPartyName(m_EditTextPartyName.getText().toString());

            SQLiteDatabaseHandler_Settings db_settings = new SQLiteDatabaseHandler_Settings(m_Context);
            db_settings.saveSettings();
        }
       else{
            Toast.makeText(MS_CashRegisterInfo.this, getResources().getString(R.string.src_GewerbenameMussAusgefülltSein), Toast.LENGTH_SHORT).show();
        }
   }

   private void getData(){
       if(GlobVar.g_ObjSession != null){
           m_EditTextHostName.setText(GlobVar.g_ObjSession.getHostName());
           m_EditTextPartyName.setText(GlobVar.g_ObjSession.getPartyName());
       }
   }

   private void dataChanged(){
        //if nothing has changed
        if(!m_EditTextHostName.getText().toString().equals(GlobVar.g_ObjSession.getHostName())
            || !m_EditTextPartyName.getText().toString().equals(GlobVar.g_ObjSession.getPartyName())){
            m_fab.setEnabled(true);
            m_fab.setAlpha(255);
        }
        else{
            m_fab.setEnabled(false);
            m_fab.setAlpha(120);
        }
   }
}
