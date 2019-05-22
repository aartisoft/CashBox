package com.example.dd.cashbox;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.SwitchCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;

import global.GlobVar;

public class MS_CashRegisterSettings extends AppCompatActivity {

    private View m_decorView;
    private SwitchCompat m_switchUseMainCash;
    private int m_uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ms_cashregistersettings);

        //init variables
        m_decorView = getWindow().getDecorView();
        m_switchUseMainCash = findViewById(R.id.ms_cashregistersett_maincashswitch);

        //set UI
        m_decorView.setSystemUiVisibility(m_uiOptions);
        Toolbar toolbar = findViewById(R.id.ms_cashregistersett_tb);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //set variables
        setUseMainCash();

        //set listener
        m_switchUseMainCash.setOnCheckedChangeListener(pawnOnCheckedChangeListener);
    }

    ///////////////////////////// LISTENER ///////////////////////////////////////////////////////////////////

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
                Intent intent = new Intent(MS_CashRegisterSettings.this, MenuSettings.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("EXTRA_SESSION_ID", 1);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private CompoundButton.OnCheckedChangeListener pawnOnCheckedChangeListener = new CompoundButton.OnCheckedChangeListener(){
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked){
                GlobVar.g_bUseMainCash = true;
            }
            else{
                GlobVar.g_bUseMainCash = false;
            }
        }
    };

    ///////////////////////////// METHODS ///////////////////////////////////////////////////////////////////
    private void setUseMainCash(){
        if(GlobVar.g_bUseMainCash){
            m_switchUseMainCash.setChecked(true);
        }
        else{
            m_switchUseMainCash.setChecked(false);
        }
    }
}
