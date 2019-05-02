package com.example.dd.cashbox;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import adapter.ListViewAllBillAdapter;
import adapter.ListViewBillAdapter;
import adapter.ViewPagerAllBillAdapter;
import adapter.ViewPagerRetoureStornoAdapter;
import fragments.ViewPagerAllBillFragment;
import global.GlobVar;
import objects.ObjBill;

public class AllBills extends AppCompatActivity {

    private Context m_Context;
    private View m_decorView;
    private ViewPagerAllBillAdapter m_ViewPagerAdapter;
    private TabLayout m_TabLayout;
    private ViewPager m_ViewPager;
    private Spinner m_Spinner_Tables;
    private Spinner m_Spinner_Date;
    private int m_iSessionTable = -1;
    private int m_iSessionBill = -1;
    private int m_iChoosenTable = -1;
    private String m_strChoosenDate = "";
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
        m_TabLayout = findViewById(R.id.activity_allbills_tab);
        m_ViewPager = findViewById(R.id.activity_allbills_viewpager);
        m_Spinner_Tables = findViewById(R.id.activity_allbills_tab_spinner_tbls);
        m_Spinner_Date = findViewById(R.id.activity_allbills_tab_spinner_date);

        //set UI
        m_decorView.setSystemUiVisibility(m_uiOptions);
        Toolbar toolbar = findViewById(R.id.activity_allbills_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        m_decorView.setSystemUiVisibility(m_uiOptions);

        //set tabs
        setTabulator();

        //set spinner
        setSpinnerTables();
        setSpinnerDates();

        //set bills
        //setBills();

        //set Listener
        m_Spinner_Tables.setOnItemSelectedListener(spinnerTablesOnItemSelectedListener);
        m_Spinner_Date.setOnItemSelectedListener(spinnerDatesOnItemSelectedListener);
    }

    private Spinner.OnItemSelectedListener spinnerTablesOnItemSelectedListener = new Spinner.OnItemSelectedListener(){

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
           m_iChoosenTable = m_Spinner_Tables.getSelectedItemPosition() - 1;
           setTabulator();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            //do nothing
        }
    };

    private Spinner.OnItemSelectedListener spinnerDatesOnItemSelectedListener = new Spinner.OnItemSelectedListener(){

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            int iChoosenDate = m_Spinner_Date.getSelectedItemPosition();
            switch(iChoosenDate) {
                case 0:
                    m_strChoosenDate = "all";
                    break;
                case 1:
                    m_strChoosenDate = "today";
                    break;
                default:
                    break;
            }

            setTabulator();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            //do nothing
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                Intent intent = new Intent(AllBills.this, Main.class);
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
        m_ViewPagerAdapter.addFragment(new ViewPagerAllBillFragment().getInstance(m_iChoosenTable, m_strChoosenDate, "all")
                , getResources().getString(R.string.src_Alle), 1, m_Context);
        m_ViewPagerAdapter.addFragment(new ViewPagerAllBillFragment().getInstance(m_iChoosenTable, m_strChoosenDate, "open")
                , getResources().getString(R.string.src_Offen), 1, m_Context);
        m_ViewPagerAdapter.addFragment(new ViewPagerAllBillFragment().getInstance(m_iChoosenTable, m_strChoosenDate, "paid")
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

    private void setSpinnerTables(){
        //create list tables
        List<String> lstTables = new ArrayList<>();
        lstTables.add(getResources().getString(R.string.src_AlleTische));
        for(int i = 0; i <= GlobVar.g_iTables; i++){
            int iTable = i+1;
            lstTables.add(getResources().getString(R.string.src_Tisch) + " " + iTable);
        }


        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, lstTables);
        m_Spinner_Tables.setAdapter(dataAdapter);

        m_Spinner_Tables.setSelection(0);
    }

    private void setSpinnerDates(){
        //create list dates
        List<String> lstDates = new ArrayList<>();
        lstDates.add(getResources().getString(R.string.src_AlleZeitraeume));
        lstDates.add(getResources().getString(R.string.src_Heute));

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, lstDates);
        m_Spinner_Date.setAdapter(dataAdapter);

        m_Spinner_Date.setSelection(0);
    }

    /*public void openBill(int position){
        // Get the selected item text from ListView
        ObjBill objBill = m_listViewBillAdapter.getObjBill(position);

        Intent intent = new Intent(AllBills.this, Main.class);
        intent.putExtra("BILL", objBill.getBillNr());
        intent.putExtra("TABLE", m_iSessionTable);
        startActivity(intent);
    }*/
}