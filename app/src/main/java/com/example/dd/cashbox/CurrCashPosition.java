package com.example.dd.cashbox;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import adapter.ViewPagerAllBillAdapter;
import fragments.AllBillsShowDialogFragment;
import fragments.ViewPagerAllBillFragment;

public class CurrCashPosition extends AppCompatActivity {

    private Context m_Context;
    private View m_decorView;
    private ViewPagerAllBillAdapter m_ViewPagerAdapter;
    private TabLayout m_TabLayout;
    private ViewPager m_ViewPager;
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allbills);

        //get intent variables
        m_iSessionTable = getIntent().getIntExtra("TABLE", -1);
        m_iSessionBill = getIntent().getIntExtra("BILL", -1);

        //init variables
        m_Context = this;
        m_decorView = getWindow().getDecorView();
        m_TabLayout = findViewById(R.id.activity_currcashpos_tab);
        m_ViewPager = findViewById(R.id.activity_currcashpos_viewpager);

        //set UI
        m_decorView.setSystemUiVisibility(m_uiOptions);
        Toolbar toolbar = findViewById(R.id.activity_currcashpos_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        m_decorView.setSystemUiVisibility(m_uiOptions);

        //set tabs
        setTabulator();

        //set Listener
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                Intent intent = new Intent(CurrCashPosition.this, Main.class);
                intent.putExtra("BILL", m_iSessionBill);
                intent.putExtra("TABLE", m_iSessionTable);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
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


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            m_decorView.setSystemUiVisibility(m_uiOptions);
        }
    }

    //////////////////////////////// METHODS ////////////////////////////////////////////////////////////////////
    private void setTabulator(){
        //setup viewpager
        FragmentManager fm = getSupportFragmentManager();
        m_ViewPagerAdapter = new ViewPagerAllBillAdapter(fm);
        m_ViewPagerAdapter.addFragment(new ViewPagerAllBillFragment().getInstance("all")
                , getResources().getString(R.string.src_Alle), 1, m_Context);
        m_ViewPagerAdapter.addFragment(new ViewPagerAllBillFragment().getInstance("open")
                , getResources().getString(R.string.src_Offen), 1, m_Context);
        m_ViewPagerAdapter.addFragment(new ViewPagerAllBillFragment().getInstance("paid")
                , getResources().getString(R.string.src_Bezahlt), 1, m_Context);

        m_ViewPager.setAdapter(m_ViewPagerAdapter);

        //setup custom view
        m_TabLayout.setupWithViewPager(m_ViewPager);
        m_TabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        View headerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.activity_allbills_tablayout, null, false);

        //set for all tabs
        for(int tabs = 0; tabs < m_TabLayout.getTabCount(); tabs++){
            m_TabLayout.getTabAt(tabs).setCustomView(m_ViewPagerAdapter.getTabView(tabs));
        }
    }
}