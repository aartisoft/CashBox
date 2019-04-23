package com.example.dd.cashbox;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;

import SQLite.SQLiteDatabaseHandler_TableBills;
import adapter.ListViewMainCashBillPayAdapter;
import adapter.RecyclerMainCashBillAdapter;
import fragments.MainCashBillDialogFragment;
import global.GlobVar;
import objects.ObjBill;
import objects.ObjBillProduct;

public class MainCash extends AppCompatActivity implements View.OnClickListener {

    private Context m_Context;
    private View m_decorView;
    private TextView m_TextViewTable;
    private TextView m_TextViewBill;
    private TextView m_TextViewOpenSum;
    private TextView m_TvToPay;
    private TextView m_TvBack;
    private EditText m_EdtWantsToPay;
    private EditText m_EdtPays;
    private Button m_btnPay;
    private Button m_btnCancel;
    private RecyclerMainCashBillAdapter m_rv_adapter;
    private ListViewMainCashBillPayAdapter m_lv_adapter;
    private RecyclerView m_recyclerview;
    private ListView m_listview;
    private double m_dToPay = 0.0;
    private double m_dWantsToPay = 0.0;
    private double m_dPays = 0.0;
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
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_cash);

        //get intent variables
        m_iSessionTable = getIntent().getIntExtra("TABLE", -1);
        m_iSessionBill = getIntent().getIntExtra("BILL", -1);

        //init variables
        m_Context = this;
        m_decorView = getWindow().getDecorView();
        m_TextViewTable = findViewById(R.id.activity_main_bill_cash_tvtable);
        m_TextViewBill = findViewById(R.id.activity_main_bill_cash_tvbill);
        m_TextViewOpenSum = findViewById(R.id.activity_main_bill_cash_tvbillsum);
        m_TvToPay = findViewById(R.id.activity_main_cash_pay_tvtopaysum);
        m_TvBack = findViewById(R.id.activity_main_cash_pay_tvrestsum);
        m_EdtWantsToPay = findViewById(R.id.activity_main_cash_pay_edtcustomerwantspaysum);
        m_EdtPays = findViewById(R.id.activity_main_cash_pay_edtcustomerpayssum);
        m_btnPay = findViewById(R.id.activity_main_cash_pay_btnpay);
        m_btnCancel = findViewById(R.id.activity_main_cash_pay_btncancel);
        m_recyclerview = findViewById(R.id.activity_main_bill_cash_rv);
        m_listview = findViewById(R.id.activity_main_cash_pay_lv);

        //set UI
        m_decorView.setSystemUiVisibility(m_uiOptions);
        Toolbar toolbar = findViewById(R.id.activity_main_cash_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //set headers
        setHeaderTable();
        setHeaderBill();

        //set Listener
        m_decorView.getViewTreeObserver().addOnGlobalLayoutListener(softkeyboardOnGlobalLayoutListener);
        m_btnPay.setOnClickListener(this);
        m_btnCancel.setOnClickListener(this);
        m_EdtPays.setOnTouchListener(paysOnTouchListener);
        m_EdtWantsToPay.setOnTouchListener(wantsToPayOnTouchListener);
        m_EdtPays.setOnEditorActionListener(paysOnKeyListener);
        m_EdtWantsToPay.setOnEditorActionListener(wantsToPayOnKeyListener);
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

            if (keypadHeight > screenHeight * 0.1) {
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
                //set pay transit false
                setPayTransitFalse();

                Intent intent = new Intent(MainCash.this, Main.class);
                intent.putExtra("BILL", m_iSessionBill);
                intent.putExtra("TABLE", m_iSessionTable);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(MainCash.this, Main.class);

        switch(v.getId()){
            case R.id.activity_main_cash_pay_btnpay:
                //set articles paid
                setPaid();

                //set pay transit false
                setPayTransitFalse();

                intent = new Intent(MainCash.this, Main.class);
                intent.putExtra("BILL", m_iSessionBill);
                intent.putExtra("TABLE", m_iSessionTable);
                startActivity(intent);
                finish();
                break;

            case R.id.activity_main_cash_pay_btncancel:
                //set pay transit false
                setPayTransitFalse();

                intent = new Intent(MainCash.this, Main.class);
                intent.putExtra("BILL", m_iSessionBill);
                intent.putExtra("TABLE", m_iSessionTable);
                startActivity(intent);
                finish();
                break;

            default:

        }
    }

    private View.OnTouchListener wantsToPayOnTouchListener = new View.OnTouchListener(){

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            m_EdtWantsToPay.setCursorVisible(true);
            return false;
        }
    };

    private View.OnTouchListener paysOnTouchListener = new View.OnTouchListener(){

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            m_EdtPays.setCursorVisible(true);
            return false;
        }
    };

    private TextView.OnEditorActionListener wantsToPayOnKeyListener = new TextView.OnEditorActionListener(){

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                m_EdtWantsToPay.setCursorVisible(false);

                String strWantsToPay = "0,0";
                strWantsToPay = m_EdtWantsToPay.getText().toString();


                if(strWantsToPay.equals("")){
                    strWantsToPay = "0,0";
                }
                strWantsToPay = strWantsToPay.replace(",", ".");
                m_dWantsToPay = Double.parseDouble(strWantsToPay);

                setChangeSum();

                //set edittext
                DecimalFormat df = new DecimalFormat("0.00");
                String strOutput = df.format(m_dWantsToPay);
                m_EdtWantsToPay.setText(strOutput);
            }
            return false;
        }
    };

    private TextView.OnEditorActionListener paysOnKeyListener = new TextView.OnEditorActionListener(){

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                m_EdtPays.setCursorVisible(false);

                String strPays = "0,0";
                strPays = m_EdtPays.getText().toString();

                if(strPays.equals("")){
                    strPays = "0,0";
                }
                strPays = strPays.replace(",", ".");
                m_dPays = Double.parseDouble(strPays);

                setChangeSum();

                //set edittext
                DecimalFormat df = new DecimalFormat("0.00");
                String strOutput = df.format(m_dPays);
                m_EdtPays.setText(strOutput);
            }
            return false;
        }
    };

    //////////////////////////////////////////// METHODS /////////////////////////////////////////////////////////////////////////////
    public void raiseChange(){
        setupRecyclerView();
        setuplistview();
        setOpenTransitSum();
    }

    public void delTransitItems(String p_strCategory, String p_strProduct){
        if(m_iSessionTable != -1 && m_iSessionBill != -1) {
            for (ObjBillProduct objBillProduct : GlobVar.g_lstTableBills.get(m_iSessionTable).get(getBillListPointer()).m_lstProducts) {
                if(objBillProduct.getCategory().equals(p_strCategory)){
                    if(objBillProduct.getProduct().getName().equals(p_strProduct)){
                        objBillProduct.setPayTransit(false);
                    }
                }
            }
        }
        raiseChange();
    }

    private void setHeaderTable(){
        String strTableHeader = "";
        if(m_iSessionTable != -1){
            strTableHeader = getResources().getString(R.string.src_Tisch) + " " + String.valueOf(m_iSessionTable+1);
            m_TextViewTable.setText(strTableHeader);
        }
        else{
            //implement failure
        }

    }

    private void setHeaderBill(){
        String strBillHeader = "";
        if(m_iSessionBill != -1){
            strBillHeader = getResources().getString(R.string.src_Beleg) + " " + String.valueOf(m_iSessionBill);
            m_TextViewBill.setText(strBillHeader);

            //set recyclerview
            setupRecyclerView();
        }
        else{
            //implement failure
        }

    }

    private void setOpenSum(){
        String strOpenSum;
        if(m_iSessionTable != -1 && m_iSessionBill != -1){
            double prize = 0.00;
            for(ObjBillProduct objBillProduct : GlobVar.g_lstTableBills.get(m_iSessionTable).get(getBillListPointer()).m_lstProducts) {
                if(!objBillProduct.getPayTransit() && !objBillProduct.getPaid()
                        && !objBillProduct.getCanceled() && !objBillProduct.getReturned()){
                    prize += objBillProduct.getVK();
                }
            }

            DecimalFormat df = new DecimalFormat("0.00");
            strOpenSum = df.format(prize);
            strOpenSum = strOpenSum + "€";
        }
        else{
            strOpenSum = "0,00€";
        }
        m_TextViewOpenSum.setText(strOpenSum);
    }

    private void setOpenTransitSum(){
        String strOpenTransitSum;
        double prize = 0.00;
        if(m_iSessionTable != -1 && m_iSessionBill != -1){
            for(ObjBillProduct objBillProduct : GlobVar.g_lstTableBills.get(m_iSessionTable).get(getBillListPointer()).m_lstProducts) {
                if(objBillProduct.getPayTransit()){
                    prize += objBillProduct.getVK();
                }
            }

            DecimalFormat df = new DecimalFormat("0.00");
            strOpenTransitSum = df.format(prize);
        }
        else{
            prize = 0.0;
            strOpenTransitSum = "0,00";
        }
        m_dToPay = prize;
        m_TvToPay.setText(strOpenTransitSum);
    }

    private void setChangeSum(){
        if(m_dPays > 0.00){
            double dChange;
            if(m_dWantsToPay > m_dToPay){
                dChange = m_dWantsToPay - m_dPays;
            }
            else{
                dChange = m_dToPay - m_dPays;
            }

            //delete sign
            if(dChange < 0){
                dChange = dChange*-1;
            }
            else{
                dChange = 0.00;
            }

            //write to textview
            DecimalFormat df = new DecimalFormat("0.00");
            String strChange = df.format(dChange);
            m_TvBack.setText(strChange);
        }
    }

    private void setupRecyclerView(){

        //write tablebills to database
        SQLiteDatabaseHandler_TableBills db_tablebills = new SQLiteDatabaseHandler_TableBills(m_Context);
        db_tablebills.addTableBill(m_iSessionTable, m_iSessionBill);

        //get list bill pointer
        final int iBill = getBillListPointer();

        m_rv_adapter = new RecyclerMainCashBillAdapter(this, GlobVar.g_lstTableBills.get(m_iSessionTable).get(iBill).m_lstProducts);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        m_recyclerview.setLayoutManager(mLayoutManager);

        m_recyclerview.setAdapter(m_rv_adapter);
        m_rv_adapter.notifyDataSetChanged();

        //set opensum
        setOpenSum();

        //set infotext mainbill
        if(GlobVar.g_lstTableBills.get(m_iSessionTable).get(iBill).m_lstProducts.size() > 0){
            boolean bFound = false;
            for(ObjBillProduct objBillProduct : GlobVar.g_lstTableBills.get(m_iSessionTable).get(iBill).m_lstProducts){
                if(!objBillProduct.getPayTransit() && !objBillProduct.getPaid()
                        && !objBillProduct.getCanceled() && !objBillProduct.getReturned()){
                    bFound = true;
                    break;
                }
            }
            if(bFound){
                findViewById(R.id.activity_main_bill_cash_rv_noitem).setVisibility(View.INVISIBLE);
            }
            else{
                findViewById(R.id.activity_main_bill_cash_rv_noitem).setVisibility(View.VISIBLE);
            }
        }
        else{
            findViewById(R.id.activity_main_bill_cash_rv_noitem).setVisibility(View.VISIBLE);
        }
    }

    private void setuplistview(){
        m_lv_adapter = new ListViewMainCashBillPayAdapter(m_Context, GlobVar.g_lstTableBills.get(m_iSessionTable).get(getBillListPointer()).m_lstProducts);
        m_listview.setAdapter(m_lv_adapter);
    }

    private int getBillListPointer(){
        //get bill
        int iBill = 0;
        for(ObjBill objBill : GlobVar.g_lstTableBills.get(m_iSessionTable)){
            if(objBill.getBillNr() == m_iSessionBill){
                return iBill;
            }
            iBill++;
        }
        return 0;
    }

    private void setPayTransitFalse(){
        for(ObjBillProduct objBillProduct : GlobVar.g_lstTableBills.get(m_iSessionTable).get(getBillListPointer()).m_lstProducts){
            objBillProduct.setPayTransit(false);
        }
    }

    private void setPaid(){
        for(ObjBillProduct objBillProduct : GlobVar.g_lstTableBills.get(m_iSessionTable).get(getBillListPointer()).m_lstProducts){
            if(objBillProduct.getPayTransit()){
                objBillProduct.setPaid(true);
            }
        }
    }

    public void showMainCashBillDialog(String p_strCategory, String p_strProduct) {
        FragmentManager fm = getSupportFragmentManager();
        MainCashBillDialogFragment mainCashBillDialogFragment = MainCashBillDialogFragment.newInstance("Some Title");

        // pass table, bill to fragment
        Bundle args = new Bundle();
        args.putString("CATEGORY", p_strCategory);
        args.putString("PRODUCT", p_strProduct);
        args.putInt("TABLE", m_iSessionTable);
        args.putInt("BILL", m_iSessionBill);

        mainCashBillDialogFragment.setArguments(args);
        mainCashBillDialogFragment.show(fm, "fragment_maincashbill");
    }
}
