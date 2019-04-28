package com.example.dd.cashbox;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ExpandableListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import adapter.ListViewAllBillAdapter;
import adapter.ListViewBillAdapter;
import global.GlobVar;
import objects.ObjBill;

public class AllBills extends AppCompatActivity {

    private Context m_Context;
    private View m_decorView;
    private ExpandableListView m_ListView;
    private ListViewAllBillAdapter m_listViewBillAdapter;
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
        setContentView(R.layout.activity_allbills);

        //get intent variables
        m_iSessionTable = getIntent().getIntExtra("TABLE", -1);
        m_iSessionBill = getIntent().getIntExtra("BILL", -1);

        //init variables
        m_Context = this;
        m_decorView = getWindow().getDecorView();
        m_ListView = findViewById(R.id.activity_allbills_lv);

        //set UI
        m_decorView.setSystemUiVisibility(m_uiOptions);
        Toolbar toolbar = findViewById(R.id.activity_allbills_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        m_decorView.setSystemUiVisibility(m_uiOptions);

        //set bills
        setBills();

        //set Listener
        m_ListView.setOnItemLongClickListener(listviewOnItemListener);
    }

    private ExpandableListView.OnItemLongClickListener listviewOnItemListener = new ExpandableListView.OnItemLongClickListener(){
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            if (ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_GROUP
                    || ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                int groupPosition = ExpandableListView.getPackedPositionGroup(id);

                // Get the selected item text from ListView
                ObjBill objBill = m_listViewBillAdapter.getObjBill(groupPosition);

                Intent intent = new Intent(AllBills.this, Main.class);
                intent.putExtra("BILL", objBill.getBillNr());
                intent.putExtra("TABLE", m_iSessionTable);
                startActivity(intent);
                return true;
            }
            return false;
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                Intent intent = new Intent(AllBills.this, Main.class);
                intent.putExtra("BILL", m_iSessionBill);
                intent.putExtra("TABLE", m_iSessionTable);
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

        Intent intent = new Intent(AllBills.this, Main.class);
        intent.putExtra("BILL", objBill.getBillNr());
        intent.putExtra("TABLE", m_iSessionTable);
        startActivity(intent);
    }

    private void setBills(){
        if(GlobVar.g_lstTableBills.size() != 0){
            m_listViewBillAdapter = new ListViewAllBillAdapter(this, GlobVar.g_lstTableBills.get(0));
            m_ListView.setAdapter(m_listViewBillAdapter);
        }
    }
}