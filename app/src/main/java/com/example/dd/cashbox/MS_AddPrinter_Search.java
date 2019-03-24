package com.example.dd.cashbox;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import SQLite.SQLiteDatabaseHandler_Printer;
import adapter.ListViewPrinterSearchAdapter;
import epson.EpsonDiscover;
import global.GlobVar;
import objects.ObjPrinter;
import objects.ObjPrinterSearch;


public class MS_AddPrinter_Search extends AppCompatActivity {

    private Runnable m_runnable;
    private final Handler m_handler = new Handler();
    private Thread m_thread;
    private Context m_Context;
    private EpsonDiscover m_epsonDiscover;
    private ArrayList<ObjPrinterSearch> m_PrinterList = null;
    private ListViewPrinterSearchAdapter m_adapter;
    private FloatingActionButton m_fab;
    private ListView m_listView;
    private View m_decorView;
    private int m_uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
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
        m_Context = this;
        m_PrinterList = new ArrayList<ObjPrinterSearch>();
        m_fab = findViewById(R.id.ms_addprinter_searchok);
        m_listView = findViewById(R.id.ms_addprinter_listview_search);
        m_decorView = getWindow().getDecorView();

        //set UI
        findViewById(R.id.ms_addprinter_listview_searchnoresult).setVisibility(View.INVISIBLE);
        m_decorView.setSystemUiVisibility(m_uiOptions);
        Toolbar toolbar = findViewById(R.id.toolbar_ms_addprinter_search);
        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        m_fab.setEnabled(false);

        //PrinterSearch
        m_epsonDiscover = new EpsonDiscover(this);
        m_epsonDiscover.startDiscovery();
        discoverPrinterHandler();

        //init Listener
        m_fab.setOnClickListener(fabOnClickListener);
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
                m_epsonDiscover.stopDiscovery();

                Intent intent = new Intent(MS_AddPrinter_Search.this, MS_AddPrinter.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private View.OnClickListener fabOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            boolean bChecked = false;
            for(int i=0;i<m_PrinterList.size();i++)
            {
                //get Object from adapter
                ObjPrinterSearch printerAdapter = m_adapter.getObjPrinter(i);
                if(m_PrinterList.get(i).isChecked()==true)
                {
                    bChecked = true;

                    //check if printer already existing
                    boolean bIsExisting = false;
                    for(ObjPrinter printer : GlobVar.g_lstPrinter){
                        if(printerAdapter.getTarget().equals(printer.getTarget())){
                            bIsExisting = true;
                            break;
                        }
                    }
                    //if printer is not existing then write into list
                    if(!bIsExisting){
                        SQLiteDatabaseHandler_Printer db = new SQLiteDatabaseHandler_Printer(m_Context);
                        ObjPrinter printer = new ObjPrinter(printerAdapter.getDeviceBrand(), printerAdapter.getDeviceName(), printerAdapter.getDeviceType(), printerAdapter.getTarget(),
                                printerAdapter.getIpAddress(), printerAdapter.getMacAddress(), printerAdapter.getBdAddress());


                        //save printer
                        GlobVar.g_lstPrinter.add(printer);
                        db.addPrinter(printer);

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
    };

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
            }
        }
        catch (Exception e) {
            Log.e("write Discovery failed", e.toString());
        }
    }

    private void discoverPrinterHandler(){
        m_runnable = new Runnable() {
            @Override
            public void run() {
                m_epsonDiscover.stopDiscovery();
                m_PrinterList = m_epsonDiscover.getPrinterList();
                writeDiscoveryResult();
            }
        };
        m_handler.postDelayed(m_runnable,5000);
    }

}
