package com.example.dd.cashbox;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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


import SQLite.SQLiteDatabaseHandler_Printer;
import adapter.ListViewPrinterDetailAdapter;
import epson.EpsonPrintTestMsg;
import global.GlobVar;
import objects.ObjPrinter;


public class MS_AddPrinter_Detail extends AppCompatActivity implements OnClickListener {

    private AlertDialog.Builder m_AlertDialogBuilder;
    private AlertDialog m_AlertDialog;
    private Menu m_Menu;
    private MenuItem m_MenuItemTestdruck;
    private MenuItem m_MenuItemStatus;
    private PrinterStatusTask m_PrinterStatusTask;
    private PrinterPrintTask m_PrinterPrintTask;
    private boolean m_bPrintStatus = false;
    private String m_strPrinterStatus = "";
    private ArrayList<HashMap<String,String>> m_lstViewAttr;
    private Context m_Context;
    private ObjPrinter m_ObjPrinter;
    private EpsonPrintTestMsg m_printer;
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
        m_AlertDialogBuilder = new AlertDialog.Builder(this);

        //set UI
        m_decorView.setSystemUiVisibility(m_uiOptions);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Toolbar toolbar = findViewById(R.id.toolbar_ms_addprinter_detail);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //set ListView
        setListView();

        //set AlertDialog
        m_AlertDialogBuilder.setTitle(getResources().getString(R.string.src_DruckerLoeschen));
        m_AlertDialogBuilder.setMessage(getResources().getString(R.string.src_SindSieSicher));
        m_AlertDialogBuilder.setPositiveButton(getResources().getString(R.string.src_Ja), dialogOnclickListener);
        m_AlertDialogBuilder.setNegativeButton(getResources().getString(R.string.src_Nein), dialogOnclickListener);
        m_AlertDialog = m_AlertDialogBuilder.create();

        //set Printer
        m_printer =  new EpsonPrintTestMsg(m_Context, m_ObjPrinter);
        m_PrinterStatusTask = new PrinterStatusTask();
        m_PrinterStatusTask.execute();
        m_PrinterPrintTask = new PrinterPrintTask();
        m_PrinterStatusTask = new PrinterStatusTask();

        //set Listener
        m_btnDel.setOnClickListener(this);
        m_decorView.setOnSystemUiVisibilityChangeListener(navbarOnSystemUiVisibilityChangeListener);
    }

    private DialogInterface.OnClickListener dialogOnclickListener = new DialogInterface.OnClickListener(){
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:

                    //delete printer
                    SQLiteDatabaseHandler_Printer db = new SQLiteDatabaseHandler_Printer(m_Context);
                    GlobVar.m_lstPrinter.remove(m_ObjPrinter);
                    db.deletePrinter(m_ObjPrinter);

                    Toast.makeText(MS_AddPrinter_Detail.this, getResources().getString(R.string.src_DruckerEntfernt), Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(MS_AddPrinter_Detail.this, MS_AddPrinter.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.ms_addprinter_detail_usermenu, menu);

        //init menu variables
        m_Menu = menu;
        m_MenuItemStatus = menu.findItem(R.id.ms_Addprinterdetail_usermenu_statusaktualisieren);
        m_MenuItemTestdruck = menu.findItem(R.id.ms_Addprinterdetail_usermenu_testdruck_menu);
        m_MenuItemTestdruck.setEnabled(false);
        m_MenuItemStatus.setEnabled(false);
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
                m_PrinterStatusTask.cancel(true);
                m_PrinterPrintTask.cancel(true);

                Intent intent = new Intent(MS_AddPrinter_Detail.this, MS_AddPrinter.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;

            case R.id.ms_Addprinterdetail_usermenu_testdruck_menu:
                m_MenuItemTestdruck.setEnabled(false);
                m_MenuItemStatus.setEnabled(false);

                m_PrinterPrintTask = new PrinterPrintTask();
                m_PrinterPrintTask.execute();
                return true;

            case R.id.ms_Addprinterdetail_usermenu_statusaktualisieren:
                m_MenuItemTestdruck.setEnabled(false);
                m_MenuItemStatus.setEnabled(false);

                m_PrinterStatusTask = new PrinterStatusTask();
                m_PrinterStatusTask.execute();

                Toast.makeText(MS_AddPrinter_Detail.this, getResources().getString(R.string.src_DruckerStatusWirdAbgefragt
                ), Toast.LENGTH_SHORT).show();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v){
        switch (v.getId()) {
            case R.id.ms_addprinter_detail_btnDel:
                m_AlertDialog.show();;
                break;

            default:
                break;
        }
    }

    private void setListView(){
        for(ObjPrinter printer : GlobVar.m_lstPrinter){
            if(m_strSessionTarget.equals(printer.getMacAddress())){

                //build data for listview
                m_lstViewAttr = new ArrayList<>();
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("typ", getResources().getString(R.string.src_Hersteller));
                hashMap.put("value", printer.getDeviceBrand());
                m_lstViewAttr.add(hashMap);

                hashMap = new HashMap<>();
                hashMap.put("typ", getResources().getString(R.string.src_DruckerName));
                hashMap.put("value", printer.getDeviceName());
                m_lstViewAttr.add(hashMap);

                hashMap = new HashMap<>();
                hashMap.put("typ",getResources().getString(R.string.src_IPAdresse));
                hashMap.put("value", printer.getIpAddress());
                m_lstViewAttr.add(hashMap);

                hashMap = new HashMap<>();
                hashMap.put("typ", getResources().getString(R.string.src_MACAdresse));
                hashMap.put("value", printer.getMacAddress());
                m_lstViewAttr.add(hashMap);

                hashMap = new HashMap<>();
                hashMap.put("typ", getResources().getString(R.string.src_DruckerStatus));
                hashMap.put("value", getResources().getString(R.string.src_BitteWarten));
                m_lstViewAttr.add(hashMap);

                m_adapter = new ListViewPrinterDetailAdapter(this, m_lstViewAttr);
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

    public class PrinterStatusTask extends AsyncTask<String, Integer, String[]> {
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String[] doInBackground(String... strings) {
            m_strPrinterStatus = m_printer.getPrinterStatus();

            if(m_strPrinterStatus.equals("")){
                m_strPrinterStatus = getResources().getString(R.string.src_Offline);
            }
            return new String[0];
        }

        @Override
        protected void onPostExecute(String[] strings) {
            int iCounter = 0;
            for(HashMap<String,String> map : m_lstViewAttr) {
                if (map.get("typ").equals(getResources().getString(R.string.src_DruckerStatus))) {
                    m_lstViewAttr.get(iCounter).put("value", m_strPrinterStatus);

                    m_listview.setAdapter(new ListViewPrinterDetailAdapter(m_Context, m_lstViewAttr));
                    m_adapter.notifyDataSetChanged();
                    break;
                }
                iCounter++;
            }

            m_MenuItemTestdruck.setEnabled(true);
            m_MenuItemStatus.setEnabled(true);
        }
    }

    public class PrinterPrintTask extends AsyncTask<String, Integer, String[]> {
        @Override
        protected String[] doInBackground(String... strings) {
            m_bPrintStatus = m_printer.runPrintTestMsgSequence();

            return new String[0];
        }

        @Override
        protected void onPostExecute(String[] strings) {
            String strPrinterError = m_printer.getPrinterError();
            String strPrinterWarning = m_printer.getPrinterWarning();

            if(m_bPrintStatus){
                Toast.makeText(MS_AddPrinter_Detail.this, "Testnachricht erfolgreich gesendet", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(MS_AddPrinter_Detail.this, "Testnachricht nicht erfolgreich gesendet \n"  + strPrinterError + " " + strPrinterWarning, Toast.LENGTH_LONG).show();
            }

            m_MenuItemTestdruck.setEnabled(true);
            m_MenuItemStatus.setEnabled(true);
        }
    }
}