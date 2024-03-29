package com.example.dd.cashbox;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import SQLite.SQLiteDatabaseHandler_UserAccounts;
import adapter.ListViewUserAccountsAdapter;
import fragments.AddNewUserDialogFragment;
import fragments.EditUserDialogFragment;
import fragments.PopUpWindowCancelOKFragment;
import global.GlobVar;
import objects.ObjUser;


public class MS_UserAccounts extends AppCompatActivity implements PopUpWindowCancelOKFragment.OnDialogCancelOkResultListener {

    private Context m_Context;
    private ArrayList<ObjUser> m_UserList = null;
    private ListViewUserAccountsAdapter m_adapter;
    private FloatingActionButton m_fab;
    private FloatingActionButton m_fabdel;
    private ListView m_listView;
    private View m_decorView;
    private Menu m_Menu;
    private MenuItem m_MenuItemUserDel;
    private Toolbar m_toolbar;
    private boolean m_bDeleteModus = false;
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
        m_fabdel = findViewById(R.id.activity_ms_useraccounts_fabdel);
        m_listView = findViewById(R.id.activity_ms_useraccounts_lv);
        m_decorView = getWindow().getDecorView();

        //set UI
        m_decorView.setSystemUiVisibility(m_uiOptions);
        m_toolbar = findViewById(R.id.activity_ms_useraccounts_tb);
        setSupportActionBar(m_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        m_fabdel.hide();

        //init list
        initList();

        //init Listener
        m_fab.setOnClickListener(fabOnClickListener);
        m_fabdel.setOnClickListener(fabdelOnClickListener);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.ms_useraccounts_edituser_usermenu, menu);

        //init menu variables
        m_Menu = menu;
        m_MenuItemUserDel = menu.findItem(R.id.ms_useraccounts_edituser_usermenu_deluser);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if(m_bDeleteModus){
                    setNormalMode();
                }
                else{
                    //set all users state delete
                    for(ObjUser objUser : GlobVar.g_lstUser){
                        objUser.setDelete(false);
                    }

                    Intent intent = new Intent(MS_UserAccounts.this, MenuSettings.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }

                return true;

            case R.id.ms_useraccounts_edituser_usermenu_deluser:
                setDeleteMode();

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private View.OnClickListener fabOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            FragmentManager fm = getSupportFragmentManager();
            AddNewUserDialogFragment addNewUserDialogFragment = AddNewUserDialogFragment.newInstance();

            addNewUserDialogFragment.show(fm, "fragment_addnewuser");
        }
    };

    private View.OnClickListener fabdelOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            FragmentManager fm = getSupportFragmentManager();
            PopUpWindowCancelOKFragment popUpWindowCancelOKFragment = PopUpWindowCancelOKFragment.newInstance();

            // pass text to fragment
            Bundle args = new Bundle();
            String strText = getResources().getString(R.string.src_NutzerWirklichEntfernen) + "\n";
            args.putString("TEXT", strText);
            args.putString("TASK", "delete");

            popUpWindowCancelOKFragment.setArguments(args);
            popUpWindowCancelOKFragment.show(fm, "fragment_popupcancelok");
        }
    };

    @Override
    public void onOkResult(String p_strTASK) {
        deleteUser();
        setNormalMode();
    }

    @Override
    public void onCancelResult() {
        setNormalMode();
    }

    ///////////////////////////////////// METHODS //////////////////////////////////////////////////////////////////

    public void openUser(int position){
        FragmentManager fm = getSupportFragmentManager();
        EditUserDialogFragment editUserDialogFragment = EditUserDialogFragment.newInstance();

        Bundle args = new Bundle();
        args.putInt("USER", position);

        editUserDialogFragment.setArguments(args);
        editUserDialogFragment.show(fm, "fragment_edituser");
    }

    public void initList(){
        m_adapter = new ListViewUserAccountsAdapter(this, GlobVar.g_lstUser);
        m_listView.setAdapter(m_adapter);
    }

    public void deleteUser(){
        //check wheater active user is checked to delete
        boolean bDelete = true;
        for(int i = 0; i < GlobVar.g_lstUser.size(); i++){
            //get Object from adapter
            ObjUser objUserAdapter = m_adapter.getObjUser(i);
            if(objUserAdapter.isChecked() && objUserAdapter.isActive()) {
                bDelete = false;
                break;
            }
        }

        if(bDelete){
            SQLiteDatabaseHandler_UserAccounts db_useraccounts = new SQLiteDatabaseHandler_UserAccounts(m_Context);

            for(int i=GlobVar.g_lstUser.size(); i-- > 0;) {
                //get Object from adapter
                ObjUser objUserAdapter = m_adapter.getObjUser(i);
                if(objUserAdapter.isChecked()) {
                    //update database
                    db_useraccounts.deleteUser(GlobVar.g_lstUser.get(i));

                    GlobVar.g_lstUser.remove(i);
                }
            }

            Toast.makeText(MS_UserAccounts.this, getResources().getString(R.string.src_NutzerEntfernt), Toast.LENGTH_SHORT).show();
            m_adapter.notifyDataSetChanged();
        }
        else{
            setNormalMode();
            Toast.makeText(MS_UserAccounts.this, getResources().getString(R.string.src_AktiveNutzerKoennenNichtEntferntWerden), Toast.LENGTH_SHORT).show();
        }
    }

    private void setNormalMode(){
        m_bDeleteModus = false;
        m_fabdel.hide();
        m_fab.show();
        m_toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);

        //set all users state normal
        for(ObjUser objUser : GlobVar.g_lstUser){
            objUser.setDelete(false);
            objUser.setChecked(false);
        }
        m_adapter.notifyDataSetChanged();
    }

    private void setDeleteMode(){
        m_bDeleteModus = true;
        //set all users state delete
        for(ObjUser objUser : GlobVar.g_lstUser){
            objUser.setDelete(true);
            objUser.setChecked(false);
        }

        m_toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);
        m_fabdel.show();
        m_fab.hide();
        m_adapter.notifyDataSetChanged();
    }
}
