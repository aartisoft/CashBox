package com.example.dd.cashbox;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;

import adapter.GridViewProductAdapter;
import adapter.GridViewTableAdapter;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import global.GlobVar;

public class MainShowTables extends AppCompatActivity {

    private Context m_Context;
    private View m_decorView;
    private GridView m_GridView;
    private GridViewTableAdapter m_gridViewTableAdapter;
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
        setContentView(R.layout.activity_main_showtables);

        //init variables
        m_Context = this;
        m_decorView = getWindow().getDecorView();
        m_GridView = findViewById(R.id.activity_main_showtables_gv);

        //set UI
        m_decorView.setSystemUiVisibility(m_uiOptions);

        //set tables
        setTables();

        //set Listener
        m_GridView.setOnItemClickListener(gvTableOnItemClickListener);
    }

    private AdapterView.OnItemClickListener gvTableOnItemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


            Intent intent = new Intent(MainShowTables.this, Main.class);
            intent.putExtra("TABLE", position);
            startActivity(intent);
        }
    };


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


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            m_decorView.setSystemUiVisibility(m_uiOptions);
        }
    }

    private  void setTables(){
        m_gridViewTableAdapter = new GridViewTableAdapter(m_Context, GlobVar.g_iTables);
        m_GridView.setAdapter(m_gridViewTableAdapter);
    }
}