package com.example.dd.cashbox;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
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
import epson.EpsonDiscovery;
import objects.Printer;

//import epson.EpsonDiscovery;

public class MS_AddPrinter_Search extends AppCompatActivity {

    //Epson
    private SimpleAdapter m_PrinterListAdapter = null;
    private Context m_Context = null;
    private com.epson.epos2.printer.Printer m_Printer = null;
    private String m_strTarget = null;
    private ArrayList<HashMap<String, String>> m_PrinterList = null;
    private HashMap<String, String> foundPrinter;

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
        m_fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorGrey)));
        m_fab.setEnabled(false);

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

        //PrinterSearch
        startDiscovery();
        /*EpsonDiscovery epsonDiscovery = new EpsonDiscovery();
        m_PrinterList = epsonDiscovery.startDiscovery(this);

        for (HashMap<String, String> entry : m_PrinterList) {
            for (String name : entry.keySet()) {
                String target = entry.get(name);

                Printer test = new Printer(name, target, "EPSON");
                m_adapter.add(test);

                System.out.println("key = " + name);
                System.out.println("name = " + target);
            }
        }
        findViewById(R.id.loadingPanel).setVisibility(View.GONE);*/

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

    private ArrayList<HashMap<String, String>> startDiscovery(){
        FilterOption filterOption = null;

        //m_PrinterList.clear();
        //m_PrinterListAdapter.notifyDataSetChanged();

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
            Log.e("start failed", e.toString());
        }

        return m_PrinterList;
    }

    private DiscoveryListener m_DiscoveryListener = new DiscoveryListener() {
        @Override
        public void onDiscovery(final DeviceInfo deviceInfo) {
            runOnUiThread(new Runnable() {
                @Override
                public synchronized void run() {
                    HashMap<String, String> item = new HashMap<String, String>();
                    item.put("PrinterName", deviceInfo.getDeviceName());
                    item.put("Target", deviceInfo.getTarget());
                    //m_PrinterList.add(item);

                    Printer test = new Printer(deviceInfo.getDeviceName(), deviceInfo.getTarget(), "EPSON");
                    m_adapter.add(test);
                    //m_PrinterListAdapter.notifyDataSetChanged();
                    findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                }
            });
        }
    };
}
