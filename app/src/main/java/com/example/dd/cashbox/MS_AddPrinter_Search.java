package com.example.dd.cashbox;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.epson.epos2.discovery.DeviceInfo;
import com.epson.epos2.discovery.Discovery;
import com.epson.epos2.discovery.DiscoveryListener;
import com.epson.epos2.discovery.FilterOption;

import java.util.ArrayList;
import java.util.HashMap;

import adapter.ListViewPrinterAdapter;
import objects.Printer;


public class MS_AddPrinter_Search extends AppCompatActivity {

    private ArrayList<HashMap<String, String>> m_PrinterList = null;
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
        m_PrinterList = new ArrayList<HashMap<String, String>>();
        m_fab = findViewById(R.id.ms_addprinter_searchok);
        m_listView = findViewById(R.id.ms_addprinter_listview_search);
        m_decorView = getWindow().getDecorView();
        m_decorView.setSystemUiVisibility(uiOptions);

        //init fab
        m_fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorGrey)));
        m_fab.setEnabled(false);

        //set header
        Toolbar toolbar = findViewById(R.id.toolbar_ms_addprinter_search);
        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //init adapter
        ArrayList<Printer> arrayOfPrinter = new ArrayList<Printer>();
        m_adapter = new ListViewPrinterAdapter(this, arrayOfPrinter);
        m_listView.setAdapter(m_adapter);

        //PrinterSearch
        startDiscovery();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopDiscovery();
            }
        }, 5000);

        m_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MS_AddPrinter_Search.this, MS_AddPrinter_Search.class);
                startActivity(intent);
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

    private void startDiscovery(){

        if(m_PrinterList != null){
            m_PrinterList.clear();
        }

        FilterOption filterOption = null;
        filterOption = new FilterOption();
        filterOption.setPortType(Discovery.PORTTYPE_ALL);
        filterOption.setDeviceModel(Discovery.MODEL_ALL);
        filterOption.setEpsonFilter(Discovery.FILTER_NONE);
        filterOption.setDeviceType(Discovery.TYPE_ALL);
        filterOption.setBroadcast("255.255.255.255");

        try {
            Discovery.start(this, filterOption, m_DiscoveryListener);
        }
        catch (Exception e) {
            Log.e("Discovery failed", e.toString());
        }
    }

    private void stopDiscovery(){
        try {
            Discovery.stop();
            writeDiscoveryResult();
        }
        catch (Exception e) {
            Log.e("stop Discovery failed", e.toString());
        }
    }

    private void writeDiscoveryResult(){
        try {
            //disable Buffer Bar
            findViewById(R.id.loadingPanel).setVisibility(View.GONE);

            if(m_PrinterList != null){
                for (HashMap<String, String> entry : m_PrinterList) {
                    for (String name : entry.keySet()) {
                        String target = entry.get(name);

                        //write into ListView
                        Printer test = new Printer(name, target);
                        m_adapter.add(test);

                        System.out.println("name = " + name);
                        System.out.println("target = " + target);
                    }
                }
            }
            else{
                //no printer found
            }
        }
        catch (Exception e) {
            Log.e("write Discovery failed", e.toString());
        }
    }

    private DiscoveryListener m_DiscoveryListener = new DiscoveryListener() {
        @Override
        public void onDiscovery(final DeviceInfo deviceInfo) {
            runOnUiThread(new Runnable() {
                @Override
                public synchronized void run() {
                    HashMap<String, String> item = new HashMap<String, String>();

                    //change string target
                    String target = deviceInfo.getTarget();
                    target = target.replace("TCP", "MAC");

                    item.put(deviceInfo.getDeviceName(), target);
                    m_PrinterList.add(item);
                }
            });
        }
    };
}
