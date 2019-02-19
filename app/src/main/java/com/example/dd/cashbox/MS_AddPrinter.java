package com.example.dd.cashbox;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import SQLite.SQLiteDatabaseHandler_Printer;
import adapter.ListViewPrinterAdapter;
import global.GlobVar;
import objects.ObjPrinter;

public class MS_AddPrinter extends AppCompatActivity {

    private Context m_Context;
    private ListViewPrinterAdapter m_adapter;
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
        setContentView(R.layout.activity_ms_addprinter);

        //init variables
        m_Context = this;
        m_fab = findViewById(R.id.ms_addprinter_add);
        m_listView = findViewById(R.id.ms_addprinter_listview);
        m_decorView = getWindow().getDecorView();

        //set UI
        m_decorView.setSystemUiVisibility(m_uiOptions);
        Toolbar toolbar = findViewById(R.id.toolbar_ms_addprinter);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //read database
        readSQLiteDB();

        //set ListView
        setListView();

        //set Listener
        m_fab.setOnClickListener(fabOnClickListener);
        m_listView.setOnItemClickListener(listviewOnItemListener);

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
                Intent intent = new Intent(MS_AddPrinter.this, MenuSettings.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void readSQLiteDB(){
        SQLiteDatabaseHandler_Printer db = new SQLiteDatabaseHandler_Printer(m_Context);
        if(GlobVar.m_lstPrinter.isEmpty()){
            GlobVar.m_lstPrinter = db.allPrinters();
        }
    }

    private void writeSQLiteDB(){
        SQLiteDatabaseHandler_Printer db = new SQLiteDatabaseHandler_Printer(m_Context);

        for(ObjPrinter printer : GlobVar.m_lstPrinter){
            db.addPrinter(printer);
        }
    }

    private void setListView(){
        if(GlobVar.m_lstPrinter.size() != 0){
            findViewById(R.id.ms_addprinter_noprinter).setVisibility(View.INVISIBLE);
            m_adapter = new ListViewPrinterAdapter(this, GlobVar.m_lstPrinter);
            m_listView.setAdapter(m_adapter);
        }
        else{
            findViewById(R.id.ms_addprinter_noprinter).setVisibility(View.VISIBLE);
        }
    }

    private View.OnClickListener fabOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(MS_AddPrinter.this, getResources().getString(R.string.src_DruckerNetzwerkGesucht), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MS_AddPrinter.this, MS_AddPrinter_Search.class);
            startActivity(intent);
        }
    };

    private AdapterView.OnItemClickListener listviewOnItemListener = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // Get the selected item text from ListView
            String selectedMAC = m_adapter.getMacAddress(position);

            Intent intent = new Intent(MS_AddPrinter.this, MS_AddPrinter_Detail.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("TARGET", selectedMAC);
            startActivity(intent);
        }
    };
}
