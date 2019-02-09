package com.example.dd.cashbox;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.widget.Toast;

import adapter.ListViewPrinterAdapter;
import epson.Epson;
import global.PrinterList;
import objects.ObjPrinter;

public class MS_AddPrinter_Detail extends AppCompatActivity implements OnClickListener {

    private ObjPrinter m_ObjPrinter;
    private String m_strSessionTarget;
    private String m_strTarget;
    private TextView m_tvName;
    private TextView m_tvTargetShown;
    private Button m_btnPrint;
    private Button m_btnDel;
    private SimpleAdapter m_adapter_target;
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
        setContentView(R.layout.activity_ms_addprinter_detail);

        //init variables
        Bundle bundle = getIntent().getExtras();
        m_strSessionTarget = bundle.getString("TARGET", "NOTARGET");
        m_decorView = getWindow().getDecorView();
        m_decorView.setSystemUiVisibility(uiOptions);
        m_tvName = findViewById(R.id.ms_addprinter_detail_name);
        m_tvTargetShown = findViewById(R.id.ms_addprinter_detail_target);
        m_btnPrint = findViewById(R.id.ms_addprinter_detail_btnPrint);
        m_btnDel = findViewById(R.id.ms_addprinter_detail_btnDel);

        //set header
        Toolbar toolbar = findViewById(R.id.toolbar_ms_addprinter_detail);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //set TextViews
        for(ObjPrinter printer : PrinterList.m_lstPrinter){
            if(m_strSessionTarget.equals(printer.getMacAddress())){
                m_tvName.setText(printer.getDeviceName());
                m_strTarget = printer.getTarget();
                m_tvTargetShown.setText(printer.getTarget());
                m_ObjPrinter = printer;
                break;
            }
        }

        //init events
        m_btnPrint.setOnClickListener(this);
        m_btnDel.setOnClickListener(this);
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
                Intent intent = new Intent(MS_AddPrinter_Detail.this, MS_AddPrinter.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v){
        switch (v.getId()) {

            case R.id.ms_addprinter_detail_btnPrint:
                Epson printer = new Epson();
                printer.printBon(m_ObjPrinter.getTarget());

                Toast.makeText(MS_AddPrinter_Detail.this, getResources().getString(R.string.src_TestnachrichtVersendet), Toast.LENGTH_SHORT).show();
                break;

            case R.id.ms_addprinter_detail_btnDel:
                PrinterList.m_lstPrinter.remove(m_ObjPrinter);
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
}