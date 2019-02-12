package com.example.dd.cashbox;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.view.View.OnClickListener;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import adapter.ListViewPrinterDetailAdapter;
import epson.EpsonPrint;
import global.GlobVar;
import objects.ObjPrinter;


public class MS_AddPrinter_Detail extends AppCompatActivity implements OnClickListener {

    private Context m_Context;
    private ObjPrinter m_ObjPrinter;
    private EpsonPrint m_printer;
    private String m_strSessionTarget;
    private ListView m_listview;
    private Button m_btnDel;
    private ListViewPrinterDetailAdapter m_adapter;
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
        setContentView(R.layout.activity_ms_addprinter_detail);


        //get activity variables
        Bundle bundle = getIntent().getExtras();
        m_strSessionTarget = bundle.getString("TARGET", "NOTARGET");

        //init variables
        m_Context = this;
        m_decorView = getWindow().getDecorView();
        m_listview = findViewById(R.id.ms_addprinter_listview_detail);
        m_btnDel = findViewById(R.id.ms_addprinter_detail_btnDel);

        //set UI
        m_decorView.setSystemUiVisibility(m_uiOptions);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Toolbar toolbar = findViewById(R.id.toolbar_ms_addprinter_detail);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //set ListView
        setListView();

        //set Printer
        new Thread(new Runnable() {
            @Override
            public void run() {
                m_printer = new EpsonPrint(m_Context, m_ObjPrinter);
            }
        }).start();
        PrinterStatus();

        //set Listener
        m_btnDel.setOnClickListener(this);
        m_decorView.setOnSystemUiVisibilityChangeListener(navbarOnSystemUiVisibilityChangeListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.user_menu, menu);
        return true;
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
                Intent intent = new Intent(MS_AddPrinter_Detail.this, MS_AddPrinter.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;

            case R.id.testdruck_menu:
                m_printer.printTestMsg();

                Toast.makeText(MS_AddPrinter_Detail.this, getResources().getString(R.string.src_TestnachrichtVersendet), Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v){
        switch (v.getId()) {

            case R.id.ms_addprinter_detail_btnDel:
                GlobVar.m_lstPrinter.remove(m_ObjPrinter);
                Toast.makeText(MS_AddPrinter_Detail.this, getResources().getString(R.string.src_DruckerEntfernt), Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(MS_AddPrinter_Detail.this, MS_AddPrinter.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                break;

            default:
                break;
        }
    }

    private void setListView(){
        for(ObjPrinter printer : GlobVar.m_lstPrinter){
            if(m_strSessionTarget.equals(printer.getMacAddress())){

                //build data for listview
                ArrayList<HashMap<String,String>> lstAttr = new ArrayList<>();
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("typ", getResources().getString(R.string.src_Hersteller));
                hashMap.put("value", printer.getDeviceBrand());
                lstAttr.add(hashMap);

                hashMap = new HashMap<>();
                hashMap.put("typ", getResources().getString(R.string.src_DruckerName));
                hashMap.put("value", printer.getDeviceName());
                lstAttr.add(hashMap);

                hashMap = new HashMap<>();
                hashMap.put("typ",getResources().getString(R.string.src_IPAdresse));
                hashMap.put("value", printer.getIpAddress());
                lstAttr.add(hashMap);

                hashMap = new HashMap<>();
                hashMap.put("typ", getResources().getString(R.string.src_MACAdresse));
                hashMap.put("value", printer.getMacAddress());
                lstAttr.add(hashMap);

                hashMap = new HashMap<>();
                hashMap.put("typ", getResources().getString(R.string.src_DruckerStatus));
                hashMap.put("value", "Momentan kein Status verfügbar");
                lstAttr.add(hashMap);

                m_adapter = new ListViewPrinterDetailAdapter(this, lstAttr);
                m_listview.setAdapter(m_adapter);

                m_ObjPrinter = printer;
                break;
            }
        }
    }

    private View.OnSystemUiVisibilityChangeListener navbarOnSystemUiVisibilityChangeListener = new View.OnSystemUiVisibilityChangeListener(){
        @Override
        public void onSystemUiVisibilityChange(int visibility)
        {
            if((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0)
            {
                m_decorView.setSystemUiVisibility(m_uiOptions);
            }
        }
    };

    private void PrinterStatus(){
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String strPrinterStatus = "";
                try{
                    strPrinterStatus = m_printer.getPrinterStatus();
                }
                catch (Exception e){

                }



                Log.e("Printer Status", strPrinterStatus);
                handler.postDelayed(this, 1500);
            }
        }, 1500);

    }
}