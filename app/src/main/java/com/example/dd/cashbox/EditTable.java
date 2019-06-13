package com.example.dd.cashbox;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import SQLite.SQLiteDatabaseHandler_TableBills;
import SQLite.SQLiteDatabaseHandler_Tables;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import global.GlobVar;
import objects.ObjBill;
import objects.ObjTable;

public class EditTable extends AppCompatActivity {

    private FloatingActionButton m_fab_minus;
    private FloatingActionButton m_fab_plus;
    private EditText m_tbTable;
    private View m_decorView;
    private int m_iTables;
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
        setContentView(R.layout.activity_edittable);

        //init variables
        m_fab_minus = findViewById(R.id.edittable_flb_minus);
        m_fab_plus = findViewById(R.id.edittable_flb_plus);
        m_tbTable = findViewById(R.id.edittable_edtxt);
        m_decorView = getWindow().getDecorView();

        //get globally saved tables
        if(GlobVar.g_iTables == -1){
            m_iTables = 0;
        }
        else{
            m_iTables = GlobVar.g_iTables +1; //global tables starts at zero
        }

        //set UI
        m_decorView.setSystemUiVisibility(m_uiOptions);
        Toolbar toolbar = findViewById(R.id.toolbar_edittable);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //set EditText
        m_tbTable.setText(String.valueOf(m_iTables), TextView.BufferType.EDITABLE);
        m_tbTable.setOnTouchListener(tableOnTouchListener);
        m_tbTable.setCursorVisible(false);


        //set Listener
        m_decorView.getViewTreeObserver().addOnGlobalLayoutListener(softkeyboardOnGlobalLayoutListener);
        m_tbTable.setOnEditorActionListener(DoneOnEditorActionListener);
        m_fab_minus.setOnClickListener(fabMinusOnClickListener);
        m_fab_plus.setOnClickListener(fabPlusOnClickListener);
    }

    //////////////////////////////////////////// LISTENER ////////////////////////////////////////////////////////////////////////////
    private View.OnClickListener fabMinusOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(m_iTables > 0) {
                int iTables = m_iTables-1;
                if(!isTableUsed(iTables)){
                    m_iTables--;
                    m_tbTable.setText(String.valueOf(m_iTables), TextView.BufferType.EDITABLE);
                }
                else{
                    Toast.makeText(EditTable.this, getResources().getString(R.string.src_TischeSindInBenutzungUndKoennenNichtReduziertWerden), Toast.LENGTH_SHORT).show();
                }
            }
        }

    };

    private View.OnClickListener fabPlusOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            m_iTables++;
            m_tbTable.setText(String.valueOf(m_iTables), TextView.BufferType.EDITABLE);
        }
    };

    private OnEditorActionListener DoneOnEditorActionListener = new OnEditorActionListener(){

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                int iTables = Integer.parseInt(m_tbTable.getText().toString());

                if(!areTableUsed(iTables)){
                    m_iTables = iTables;
                    m_tbTable.setText(String.valueOf(m_iTables), TextView.BufferType.EDITABLE);
                }
                else{
                    m_tbTable.setText(String.valueOf(m_iTables), TextView.BufferType.EDITABLE);
                    Toast.makeText(EditTable.this, getResources().getString(R.string.src_TischeSindInBenutzungUndKoennenNichtReduziertWerden), Toast.LENGTH_SHORT).show();
                }

                m_tbTable.setCursorVisible(false);
            }
            return false;
        }
    };

    private View.OnTouchListener tableOnTouchListener = new View.OnTouchListener(){

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            m_tbTable.setCursorVisible(true);
            return false;
        }
    };

    private ViewTreeObserver.OnGlobalLayoutListener softkeyboardOnGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener(){
        @Override
        public void onGlobalLayout() {
            Rect r = new Rect();
            m_decorView.getWindowVisibleDisplayFrame(r);
            int screenHeight = m_decorView.getRootView().getHeight();

            // r.bottom is the position above soft keypad or device button.
            // if keypad is shown, the r.bottom is smaller than that before.
            int keypadHeight = screenHeight - r.bottom;

            //Log.d(TAG, "keypadHeight = " + keypadHeight);

            if (keypadHeight > screenHeight * 0.5) {
                // keyboard is opened
            }
            else {
                //keyboard is closed
                m_decorView.setSystemUiVisibility(m_uiOptions);
            }
        }
    };

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

                if(m_iTables != -1){
                    GlobVar.g_iTables = m_iTables -1; //starts at zero
                }

                //set global list tablebills
                setListTableBills();

                //set tables in sql database
                SQLiteDatabaseHandler_Tables db_tables = new SQLiteDatabaseHandler_Tables(this);
                if(db_tables.isDatabaseEmpty()){
                    db_tables.setTables(GlobVar.g_iTables);
                }
                else{
                    db_tables.updateTables(GlobVar.g_iTables);
                }


                Intent intent = new Intent(EditTable.this, Main.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("EXTRA_SESSION_ID", 1);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //////////////////////////////////////////// METHODS ////////////////////////////////////////////////////////////////////////////
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


    private void setListTableBills(){
        //init global list
        for (int iTables = 0; iTables <= GlobVar.g_iTables; iTables++){

            //init table
            ObjTable objTable = new ObjTable();
            String strTableName = getResources().getString(R.string.src_Tisch) + " " + iTables+1;
            objTable.setTableName(strTableName);
            objTable.g_lstBills = new ArrayList<>();
            GlobVar.g_lstTables.add(objTable);
        }
    }

    private boolean isTableUsed(int p_iTable){
        /*if(p_iTable <= GlobVar.g_iTables){
            if(GlobVar.g_lstTableBills.get(p_iTable).size() > 0){
                return true;
            }
            else{
                return false;
            }
        }*/
        return false;
    }

    private boolean areTableUsed(int p_iTable){
        /*if(p_iTable <= GlobVar.g_iTables){
            for(int i = p_iTable; i < m_iTables; i++){
                if(GlobVar.g_lstTableBills.get(i).size() > 0){
                    return true;
                }
            }
        }*/
        return false;
    }
}
