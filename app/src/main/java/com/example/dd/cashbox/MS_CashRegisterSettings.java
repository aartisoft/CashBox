package com.example.dd.cashbox;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import SQLite.SQLiteDatabaseHandler_Session;
import global.GlobVar;
import objects.ObjPrinter;

public class MS_CashRegisterSettings extends AppCompatActivity {

    private View m_decorView;
    private Context m_Context;
    private SwitchCompat m_switchUseMainCash;
    private SwitchCompat m_switchUseSyncBon;
    private TextView m_TextViewSyncBon;
    private TextView m_TextViewPrinter;
    private Spinner m_Spinner_Printer;
    private boolean m_bUpdate = false;
    private int m_uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ms_cashregistersettings);

        //init variables
        m_Context = this;
        m_decorView = getWindow().getDecorView();
        m_TextViewPrinter = findViewById(R.id.ms_cashregistersett_tvprinter);
        m_TextViewSyncBon = findViewById(R.id.ms_cashregistersett_tvsyncbon);
        m_Spinner_Printer = findViewById(R.id.ms_cashregistersett_spinnerprinter);
        m_switchUseMainCash = findViewById(R.id.ms_cashregistersett_maincashswitch);
        m_switchUseSyncBon = findViewById(R.id.ms_cashregistersett_snybonswitch);

        //set UI
        m_decorView.setSystemUiVisibility(m_uiOptions);
        Toolbar toolbar = findViewById(R.id.ms_cashregistersett_tb);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //set variables
        setUseMainCash();

        //set spinnerprinter
        //if used as main cash register
        if(GlobVar.g_bUseMainCash){
            setSpinnerPrinter();
        }
        else{
            m_TextViewPrinter.setVisibility(View.GONE);
            m_Spinner_Printer.setVisibility(View.GONE);
            m_TextViewSyncBon.setVisibility(View.GONE);
        }


        //set listener
        m_switchUseMainCash.setOnCheckedChangeListener(mainCashOnCheckedChangeListener);
        m_switchUseSyncBon.setOnCheckedChangeListener(syncBonOnCheckedChangeListener);
        m_Spinner_Printer.setOnItemSelectedListener(printeronItemSelectedListener);
    }

    ///////////////////////////// LISTENER ///////////////////////////////////////////////////////////////////

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
                Intent intent = new Intent(MS_CashRegisterSettings.this, MenuSettings.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("EXTRA_SESSION_ID", 1);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private Spinner.OnItemSelectedListener printeronItemSelectedListener = new Spinner.OnItemSelectedListener(){

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            //get object printer
            ObjPrinter foundPrinter = new ObjPrinter();
            foundPrinter = null;
            String spinnerprinter = m_Spinner_Printer.getSelectedItem().toString();
            String macadress = spinnerprinter.substring(spinnerprinter.indexOf(":") + 1);
            for (ObjPrinter printer : GlobVar.g_lstPrinter) {
                if (printer.getMacAddress().equals(macadress)) {
                    foundPrinter = printer;
                    break;
                }
            }

            //save printer globally
            if(m_bUpdate){
                GlobVar.g_objPrinter = foundPrinter;
                updateDatabase();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private CompoundButton.OnCheckedChangeListener mainCashOnCheckedChangeListener = new CompoundButton.OnCheckedChangeListener(){
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            //setting is only available if cash register has not been used yet
            if(GlobVar.g_lstTableBills.size() == 0){
                if(isChecked){
                    GlobVar.g_bUseMainCash = true;
                    m_TextViewPrinter.setVisibility(View.VISIBLE);
                    m_Spinner_Printer.setVisibility(View.VISIBLE);
                    m_TextViewSyncBon.setVisibility(View.VISIBLE);
                    m_switchUseSyncBon.setVisibility(View.VISIBLE);
                    setSpinnerPrinter();

                    updateDatabase();
                }
                else{
                    GlobVar.g_bUseMainCash = false;
                    m_TextViewPrinter.setVisibility(View.GONE);
                    m_Spinner_Printer.setVisibility(View.GONE);
                    m_TextViewSyncBon.setVisibility(View.GONE);
                    m_switchUseSyncBon.setVisibility(View.GONE);

                    updateDatabase();
                }
            }
            else{
                //reset switch
                m_switchUseMainCash.setChecked(GlobVar.g_bUseMainCash);

                Toast.makeText(MS_CashRegisterSettings.this, getResources().getString(R.string.src_KasseMussFuerDieseUmstellungUnbenutztSein), Toast.LENGTH_SHORT).show();
            }
            
        }
    };

    private CompoundButton.OnCheckedChangeListener syncBonOnCheckedChangeListener = new CompoundButton.OnCheckedChangeListener(){
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            //setting is only available if cash register has not been used yet
            if(GlobVar.g_lstTableBills.size() == 0){
                if(isChecked){
                    GlobVar.g_bUseSyncBon = true;

                    updateDatabase();
                }
                else{
                    GlobVar.g_bUseSyncBon = false;

                    updateDatabase();
                }
            }
            else{
                //reset switch
                m_switchUseMainCash.setChecked(GlobVar.g_bUseMainCash);

                Toast.makeText(MS_CashRegisterSettings.this, getResources().getString(R.string.src_KasseMussFuerDieseUmstellungUnbenutztSein), Toast.LENGTH_SHORT).show();
            }

        }
    };

    ///////////////////////////// METHODS ///////////////////////////////////////////////////////////////////
    private void setUseMainCash(){
        if(GlobVar.g_bUseMainCash){
            m_switchUseMainCash.setChecked(true);
            m_TextViewSyncBon.setVisibility(View.VISIBLE);
            m_switchUseSyncBon.setVisibility(View.VISIBLE);

            if(GlobVar.g_bUseSyncBon){
                m_switchUseSyncBon.setChecked(true);
            }
            else{
                m_switchUseSyncBon.setChecked(false);
            }
        }
        else{
            m_switchUseMainCash.setChecked(false);
            m_switchUseSyncBon.setVisibility(View.GONE);
            m_TextViewSyncBon.setVisibility(View.GONE);
        }
    }

    private void setSpinnerPrinter(){

        //if not choosen yet
        if(GlobVar.g_objPrinter == null){
            m_Spinner_Printer.setPrompt(getResources().getString(R.string.src_DruckerAuswaehlen));

            List<String> categories = new ArrayList<>();
            categories.add(getResources().getString(R.string.src_KeinenDruckerAusgewählt));

            if(!GlobVar.g_lstPrinter.isEmpty()){
                for(ObjPrinter printer : GlobVar.g_lstPrinter){
                    categories.add(printer.getDeviceName() + " - MAC:" + printer.getMacAddress());
                }
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, categories);
                m_Spinner_Printer.setAdapter(dataAdapter);
            }
            else{
                categories.add(getResources().getString(R.string.src_KeineDruckerVorhanden));
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
                m_Spinner_Printer.setAdapter(dataAdapter);
            }
        }
        else{
            ObjPrinter p_printer = GlobVar.g_objPrinter;
            m_Spinner_Printer.setPrompt(getResources().getString(R.string.src_DruckerAuswaehlen));
            int printer_position = 0;

            if(!GlobVar.g_lstPrinter.isEmpty()){
                List<String> categories = new ArrayList<>();
                categories.add(getResources().getString(R.string.src_KeinenDruckerAusgewählt));

                int counter = 1;
                for(ObjPrinter printer : GlobVar.g_lstPrinter){
                    //get position of choosen printer
                    if(p_printer != null){
                        if(printer.getMacAddress().equals(p_printer.getMacAddress())){
                            printer_position = counter;
                        }
                    }

                    categories.add(printer.getDeviceName() + " - MAC:" + printer.getMacAddress());
                    counter++;
                }

                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, categories);
                m_Spinner_Printer.setAdapter(dataAdapter);

                m_Spinner_Printer.setSelection(printer_position);
            }
            else{
                List<String> categories = new ArrayList<>();
                categories.add(getResources().getString(R.string.src_KeineDruckerVorhanden));
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
                m_Spinner_Printer.setAdapter(dataAdapter);
            }
        }
        m_bUpdate = true;

    }

    private void updateDatabase(){
        SQLiteDatabaseHandler_Session db_session = new SQLiteDatabaseHandler_Session(m_Context);
        db_session.saveSession();
    }
}
