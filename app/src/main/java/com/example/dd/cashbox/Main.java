package com.example.dd.cashbox;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

public class Main extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private int m_iSessionId = 0;
    private View m_decorView;
    private NavigationView m_navigationView;
    private DrawerLayout m_DrawerLayout;
    private int m_uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //activity variables
        m_iSessionId = getIntent().getIntExtra("EXTRA_SESSION_ID", 0);

        //init variables
        m_decorView = getWindow().getDecorView();
        m_DrawerLayout = findViewById(R.id.drawer_layout);
        m_navigationView = findViewById(R.id.nav_view);

        //set UI
        m_decorView.setSystemUiVisibility(m_uiOptions);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);

        //set Listener
        m_navigationView.setNavigationItemSelectedListener(this);

        //open Drawer
        if(m_iSessionId == 1){
            m_DrawerLayout.openDrawer(GravityCompat.START);
        }
    }

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
                m_DrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        m_DrawerLayout= findViewById(R.id.drawer_layout);
        m_DrawerLayout.closeDrawer(GravityCompat.START);

        switch (item.getItemId()) {
            case R.id.nav_einstellungen:
                startActivity(new Intent(this, MenuSettings.class));
                return true;

            case R.id.nav_tische:
                startActivity(new Intent(this, EditTable.class));
                return true;

            case R.id.nav_artikel:
                startActivity(new Intent(this, EditCategory.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
