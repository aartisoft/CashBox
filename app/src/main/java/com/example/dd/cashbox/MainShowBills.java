package com.example.dd.cashbox;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.ListView;

import adapter.GridViewTableAdapter;
import adapter.ListViewBillAdapter;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import global.GlobVar;
import objects.ObjBill;

public class MainShowBills extends AppCompatActivity {

    private Context m_Context;
    private View m_decorView;
    private ExpandableListView m_ListView;
    private ListViewBillAdapter m_listViewBillAdapter;
    private int m_iSessionTable = -1;
    private int m_iSessionBill = -1;
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
        m_iSessionTable = GlobVar.g_iSessionTable;
        m_iSessionBill = GlobVar.g_iSessionBill;

        //init variables
        m_Context = this;
        m_decorView = getWindow().getDecorView();
        m_ListView = findViewById(R.id.activity_main_showbills_lv);

        //set UI
        m_decorView.setSystemUiVisibility(m_uiOptions);
        Toolbar toolbar = findViewById(R.id.activity_main_showbills_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        m_decorView.setSystemUiVisibility(m_uiOptions);

        //set bills
        setBills();

        //set Listener
        //m_ListView.setOnItemLongClickListener(listviewOnItemListener);
    }

    //no longer needed
    /*private ExpandableListView.OnItemLongClickListener listviewOnItemListener = new ExpandableListView.OnItemLongClickListener(){
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            if (ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_GROUP
                    || ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                int groupPosition = ExpandableListView.getPackedPositionGroup(id);

                // Get the selected item text from ListView
                ObjBill objBill = m_listViewBillAdapter.getObjBill(groupPosition);

                Intent intent = new Intent(MainShowBills.this, Main.class);
                GlobVar.g_iSessionBill = objBill.getBillNr();
                GlobVar.g_iSessionTable = m_iSessionBill;
                startActivity(intent);
                return true;
            }
            return false;
        }
    };*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                Intent intent = new Intent(MainShowBills.this, Main.class);
                GlobVar.g_iSessionTable = m_iSessionTable;
                GlobVar.g_iSessionBill = m_iSessionBill;
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
    public void openBill(int position){
        // Get the selected item text from ListView
        ObjBill objBill = m_listViewBillAdapter.getObjBill(position);

        Intent intent = new Intent(MainShowBills.this, Main.class);
        GlobVar.g_iSessionTable = m_iSessionTable;
        GlobVar.g_iSessionBill = objBill.getBillNr();
        startActivity(intent);
    }

    private void setBills(){
        if(GlobVar.g_lstTables.size() != 0){
            m_listViewBillAdapter = new ListViewBillAdapter(this, GlobVar.g_lstTables.get(m_iSessionTable).g_lstBills);
            m_ListView.setAdapter(m_listViewBillAdapter);
        }
    }
}