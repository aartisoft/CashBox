package com.example.dd.cashbox;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import adapter.ListViewPrinterAdapter;
import objects.Printer;

public class MS_AddPrinter_Search extends AppCompatActivity {

    private ListViewPrinterAdapter m_adapter;
    private ListAdapter m_lstAdapter;
    private FloatingActionButton m_fab;
    private ListView m_listView;
    private View m_decorView;
    private int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ms_addprinter_search);

        //init variables
        m_fab = (FloatingActionButton) findViewById(R.id.ms_addprinter_searchok);
        m_listView = (ListView) findViewById(R.id.ms_addprinter_listview_search);
        m_decorView = getWindow().getDecorView();
        m_decorView.setSystemUiVisibility(uiOptions);

        //init fab
        //m_fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorGrey)));
        //m_fab.setEnabled(false);

        //set header
        Toolbar toolbar = findViewById(R.id.toolbar_ms_addprinter_search);
        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Construct the data source
        ArrayList<Printer> arrayOfPrinter = new ArrayList<Printer>();
        // Create the adapter to convert the array to views
        m_adapter = new ListViewPrinterAdapter(this, arrayOfPrinter);
        // Attach the adapter to a ListView
        m_listView.setAdapter(m_adapter);

        //findViewById(R.id.loadingPanel).setVisibility(View.GONE);

        m_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(MS_AddPrinter_Search.this, MS_AddPrinter_Search.class);
                //startActivity(intent);

                //NUR ZUM TESTEN
                findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                Printer test = new Printer("Speisedrucker", "192.168.179.39", "EPSON");
                m_adapter.add(test);
            }
        });

        // Set an item click listener for ListView
        /*m_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item text from ListView
                String selectedItem = (String) parent.getItemAtPosition(position);

                if (selectedItem.equals(R.string.src_DruckerHinzufuegen)) {
                    //startActivity(new Intent(this, MenuSettings.class));
                }
            }
        });*/
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus){
            m_decorView.setSystemUiVisibility(uiOptions);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(MS_AddPrinter_Search.this, MS_AddPrinter.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
