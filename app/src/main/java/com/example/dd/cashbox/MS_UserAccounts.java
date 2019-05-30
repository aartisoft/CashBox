package com.example.dd.cashbox;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import SQLite.SQLiteDatabaseHandler_Settings;
import adapter.ListViewUserAccountsAdapter;
import global.GlobVar;
import objects.ObjUser;


public class MS_UserAccounts extends AppCompatActivity {

    private Context m_Context;
    private ArrayList<ObjUser> m_UserList = null;
    private ListViewUserAccountsAdapter m_adapter;
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
        setContentView(R.layout.activity_ms_useraccounts);

        //init variables
        m_Context = this;
        m_UserList = new ArrayList<ObjUser>();
        m_fab = findViewById(R.id.activity_ms_useraccounts_fabadd);
        m_listView = findViewById(R.id.activity_ms_useraccounts_lv);
        m_decorView = getWindow().getDecorView();

        //set UI
        m_decorView.setSystemUiVisibility(m_uiOptions);
        Toolbar toolbar = findViewById(R.id.activity_ms_useraccounts_tb);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //init list
        initList();

        //init Listener
        m_fab.setOnClickListener(fabOnClickListener);
    }

    ///////////////////////////////////// LISTENER //////////////////////////////////////////////////////////////////

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
                Intent intent = new Intent(MS_UserAccounts.this, MenuSettings.class);
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

        }
    };

    ///////////////////////////////////// METHODS //////////////////////////////////////////////////////////////////

    public void openUser(int position){

    }

    public void saveUser(){
        for(int i=0;i<m_UserList.size();i++)
        {
            //get Object from adapter
            ObjUser objUserAdapter = m_adapter.getObjUser(i);
            if(m_UserList.get(i).isChecked()==true) {
                //save user
                GlobVar.g_ObjSession.setCashierName(objUserAdapter.getUserName());

                //save to database
                SQLiteDatabaseHandler_Settings db_session = new SQLiteDatabaseHandler_Settings(m_Context);
                db_session.saveSettings();
            }
        }
    }

    private void initList(){
        //TODO
        //delete if startprocess is implemented
        if(GlobVar.g_lstUser.size() == 0){
            ObjUser objUser = new ObjUser();
            objUser.setUserName("TestUser");
            GlobVar.g_lstUser.add(objUser);
        }
        m_adapter = new ListViewUserAccountsAdapter(this, GlobVar.g_lstUser);
        m_listView.setAdapter(m_adapter);
    }
}
