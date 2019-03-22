package com.example.dd.cashbox;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import adapter.GridViewTableAdapter;
import adapter.ListViewBillAdapter;
import androidx.appcompat.app.AppCompatActivity;
import global.GlobVar;
import objects.ObjBill;

public class MainShowBills extends AppCompatActivity {

    private Context m_Context;
    private View m_decorView;
    private int m_iSessionTable;
    private ListView m_ListView;
    private ListViewBillAdapter m_listViewBillAdapter;
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
        setContentView(R.layout.activity_main_showbills);

        //get intent variables
        m_iSessionTable = getIntent().getIntExtra("TABLE", 0);

        //init variables
        m_Context = this;
        m_decorView = getWindow().getDecorView();
        m_ListView = findViewById(R.id.activity_main_showbills_lv);

        //set UI
        m_decorView.setSystemUiVisibility(m_uiOptions);

        //set bills
        setBills();

        //set Listener
        m_ListView.setOnItemClickListener(listviewOnItemListener);
    }

    private AdapterView.OnItemClickListener listviewOnItemListener = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // Get the selected item text from ListView
            ObjBill objBill = m_listViewBillAdapter.getObjBill(position);

            Intent intent = new Intent(MainShowBills.this, Main.class);
            intent.putExtra("BILL", objBill.getBillNr());
            startActivity(intent);
        }
    };


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

    private void setBills(){
        if(GlobVar.g_lstTableBills.size() != 0){
            findViewById(R.id.activity_main_showbills_nobills).setVisibility(View.INVISIBLE);
            m_listViewBillAdapter = new ListViewBillAdapter(this, GlobVar.g_lstTableBills.get(m_iSessionTable));
            m_ListView.setAdapter(m_listViewBillAdapter);
        }
        else{
            findViewById(R.id.ms_addprinter_noprinter).setVisibility(View.VISIBLE);
        }
    }
}