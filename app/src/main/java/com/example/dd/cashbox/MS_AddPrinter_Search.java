package com.example.dd.cashbox;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.epson.epos2.discovery.DeviceInfo;
import com.epson.epos2.discovery.Discovery;
import com.epson.epos2.discovery.DiscoveryListener;
import com.epson.epos2.discovery.FilterOption;

import java.util.ArrayList;

import adapter.ListViewPrinterSearchAdapter;
import global.GlobVar;
import objects.ObjPrinter;
import objects.ObjPrinterSearch;


public class MS_AddPrinter_Search extends AppCompatActivity {

    private Runnable runnable;
    private Handler handler;
    private Context m_Context;
    private ArrayList<ObjPrinterSearch> m_PrinterList = null;
    private ListViewPrinterSearchAdapter m_adapter;
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

        findViewById(R.id.ms_addprinter_listview_searchnoresult).setVisibility(View.INVISIBLE);

        //init variables
        m_Context = this;
        m_PrinterList = new ArrayList<ObjPrinterSearch>();
        m_fab = findViewById(R.id.ms_addprinter_searchok);
        m_listView = findViewById(R.id.ms_addprinter_listview_search);
        m_decorView = getWindow().getDecorView();
        m_decorView.setSystemUiVisibility(uiOptions);

        //set header
        Toolbar toolbar = findViewById(R.id.toolbar_ms_addprinter_search);
        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //init fab
        m_fab.setEnabled(false);

        //PrinterSearch
        startDiscovery();

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                stopDiscovery();
            }
        };
        handler.postDelayed(runnable,5000);

        m_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean bChecked = false;
                for(int i=0;i<m_adapter.mCheckStates.size();i++)
                {
                    if(m_adapter.mCheckStates.get(i)==true)
                    {
                        bChecked = true;

                        //check if printer already existing
                        boolean bIsExisting = false;
                        for(ObjPrinter printer : GlobVar.m_lstPrinter){
                            if(m_adapter.getTarget(i).equals(printer.getTarget())){
                                bIsExisting = true;
                                break;
                            }
                        }
                        //if printer is not existing then write into list
                        if(!bIsExisting){
                            ObjPrinter printer = new ObjPrinter();
                            printer.setPrinter(m_adapter.getDeviceName(i), m_adapter.getDeviceType(i), m_adapter.getTarget(i),
                                                m_adapter.getIpAddress(i), m_adapter.getMacAddress(i), m_adapter.getBdAddress(i), "");

                            GlobVar.m_lstPrinter.add(printer);
                            Toast.makeText(MS_AddPrinter_Search.this, getResources().getString(R.string.src_DruckerHinzugefuegt), Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(MS_AddPrinter_Search.this, getResources().getString(R.string.src_DruckerBereitsVorhanden), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                if(bChecked){
                    Intent intent = new Intent(MS_AddPrinter_Search.this, MS_AddPrinter.class);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(MS_AddPrinter_Search.this, getResources().getString(R.string.src_KeineDruckerausgewaehlt), Toast.LENGTH_SHORT).show();
                }
            }
        });
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
                handler.removeCallbacks(runnable);

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
            m_fab.setEnabled(true);

            if(m_PrinterList != null && m_PrinterList.size() != 0) {
                //init adapter
                ArrayList<ObjPrinterSearch> arrayOfPrinter = new ArrayList<ObjPrinterSearch>();
                m_adapter = new ListViewPrinterSearchAdapter(this, m_PrinterList);
                m_listView.setAdapter(m_adapter);
            }
            else{
                findViewById(R.id.ms_addprinter_listview_searchnoresult).setVisibility(View.VISIBLE);
                Toast.makeText(MS_AddPrinter_Search.this, getResources().getString(R.string.src_KeineDruckerGefunden), Toast.LENGTH_SHORT).show();
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
                    //change string target
                    String name = deviceInfo.getDeviceName();
                    String target = deviceInfo.getTarget();

                    String targetshown = target.replace("TCP:", "MAC-Adresse: ");

                    ObjPrinterSearch printer = new ObjPrinterSearch(deviceInfo.getDeviceName(), deviceInfo.getDeviceType(),
                            deviceInfo.getTarget(), deviceInfo.getIpAddress(),
                            deviceInfo.getMacAddress(), deviceInfo.getBdAddress(), false);


                    m_PrinterList.add(printer);
                }
            });
        }
    };
}
