package com.example.dd.cashbox;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import SQLite.SQLiteDatabaseHandler_TableBills;
import adapter.ListViewMainCashBillPayAdapter;
import adapter.RecyclerViewMainCashBillAdapter;
import fragments.MainCashBillDialogFragment;
import fragments.PopUpWindowCancelOKFragment;
import fragments.PopUpWindowMainCashPrintBillFragment;
import global.GlobVar;
import objects.ObjBill;
import objects.ObjBillProduct;
import objects.ObjMainBillProduct;
import objects.ObjMainCashBillProduct;
import objects.ObjPrinter;

public class MainCash extends AppCompatActivity implements View.OnClickListener, PopUpWindowCancelOKFragment.OnDialogCancelOkResultListener
                                                            , PopUpWindowMainCashPrintBillFragment.OnDialogMainCashCancelOkResultListener{

    private Context m_Context;
    private View m_decorView;
    private Menu m_Menu;
    private MenuItem m_MenuItemSplitBill;
    private TextView m_TextViewTable;
    private TextView m_TextViewBill;
    private TextView m_TextViewOpenSum;
    private TextView m_TvToPay;
    private TextView m_TvBack;
    private EditText m_EdtWantsToPay;
    private EditText m_EdtPays;
    private Button m_btnPay;
    private Button m_btnCancel;
    private RecyclerViewMainCashBillAdapter m_rv_adapter;
    private List<ObjMainBillProduct> m_ListObjMainBillProduct = new ArrayList<>();
    private ListViewMainCashBillPayAdapter m_lv_adapter;
    private List<ObjMainCashBillProduct> m_ListObjMainCashBillProduct = new ArrayList<>();
    private RecyclerView m_recyclerview;
    private ListView m_listview;
    private double m_dToPay = 0.0;
    private double m_dWantsToPay = 0.0;
    private double m_dPays = 0.0;
    private double m_dChange = 0.0;
    private double m_dTip = 0.0;
    private int m_iSessionTable = -1;
    private int m_iSessionBill = -1;
    private int m_iSessionBillOLD = -1;
    private boolean m_bBillSplit = false;
    private boolean m_bKeyboardClosed = true;
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
        m_iSessionTable = GlobVar.g_iSessionTable;
        m_iSessionBill = GlobVar.g_iSessionBill;

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
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Toolbar toolbar = findViewById(R.id.activity_main_cash_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //set headers
        setHeaderTable();
        setHeaderBill();

        //init adapter
        initListViewMainCashBill();
        initRecyclerViewMainBillProduct();

        //init session variables
        m_iSessionBillOLD = m_iSessionBill;

        //set items
        //if used as main cash register
        if(GlobVar.g_bUseMainCash){
            transferAllItems();
        }

        //set Listener
        m_decorView.getViewTreeObserver().addOnGlobalLayoutListener(softkeyboardOnGlobalLayoutListener);
        m_btnPay.setOnClickListener(this);
        m_btnCancel.setOnClickListener(this);
        m_EdtPays.setOnTouchListener(paysOnTouchListener);
        m_EdtWantsToPay.setOnTouchListener(wantsToPayOnTouchListener);
        m_EdtPays.setOnEditorActionListener(paysOnKeyListener);
        m_EdtWantsToPay.setOnEditorActionListener(wantsToPayOnKeyListener);
        m_TextViewBill.setOnLongClickListener(tvBillOnLongClickListener);
    }

    //////////////////////////////////////////// LISTENER /////////////////////////////////////////////////////////////////////////////

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
                m_bKeyboardClosed = false;
            }
            else {
                //keyboard is closed
                m_bKeyboardClosed = true;
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.maincash_usermenu, menu);

        //init menu variables
        m_Menu = menu;
        m_MenuItemSplitBill = menu.findItem(R.id.maincash_usermenu_splitbill);
        m_MenuItemSplitBill.setEnabled(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //set pay transit false
                setPayTransitFalse();

                Intent intent = new Intent(MainCash.this, Main.class);
                GlobVar.g_iSessionTable = m_iSessionTable;
                GlobVar.g_iSessionBill = m_iSessionBill;
                startActivity(intent);
                finish();
                return true;

            case R.id.maincash_usermenu_splitbill:
                FragmentManager fm = getSupportFragmentManager();
                PopUpWindowCancelOKFragment popUpWindowCancelOKFragment = PopUpWindowCancelOKFragment.newInstance();

                // pass text to fragment
                Bundle args = new Bundle();
                String strText = getResources().getString(R.string.src_BelegWirklichSplitten) + "\n";
                args.putString("TEXT", strText);
                args.putString("TASK", "split");

                popUpWindowCancelOKFragment.setArguments(args);
                popUpWindowCancelOKFragment.show(fm, "fragment_popupcancelok");

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.activity_main_cash_pay_btnpay:
                startPayment();
                break;

            case R.id.activity_main_cash_pay_btncancel:
                //set pay transit false
                setPayTransitFalse();

                Intent intent = new Intent(MainCash.this, Main.class);
                intent = new Intent(MainCash.this, Main.class);
                GlobVar.g_iSessionTable = m_iSessionTable;
                GlobVar.g_iSessionBill = m_iSessionBill;
                startActivity(intent);
                finish();
                break;

            default:

        }
    }

    @Override
    public void onOkResult(String p_strTASK) {
        switch(p_strTASK) {
            case "split":
               splitBill();
                break;

            default:
                break;
        }
    }

    @Override
    public void onCancelResult() {
        //do nothing
    }

    @Override
    public void onMainCashOkResult(String p_strTASK, boolean p_bprint, ObjPrinter p_objPrinter, boolean p_bEcBill) {
        switch(p_strTASK) {
            case "pay":
                //set articles paid
                setPaid();

                //set pay transit false
                setPayTransitFalse();

                Intent intent = new Intent(MainCash.this, Main.class);
                intent = new Intent(MainCash.this, Main.class);
                GlobVar.g_iSessionTable = m_iSessionTable;

                //if bill is completely empty, then close it
                if (isBillEmpty(m_iSessionBillOLD)) {
                    GlobVar.g_iSessionBill = -2;
                }
                else {
                    GlobVar.g_iSessionBill = m_iSessionBillOLD;
                }

                //if print bill
                if(p_bprint){
                    //print bill
                    //((Main) ).addPrintJobBill(p_bEcBill, p_objPrinter);
                    //Main.
                }

                startActivity(intent);
                finish();
            break;

            default:
                break;
        }
    }

    @Override
    public void onMainCashCancelResult() {
        //do nothing
    }

    private View.OnLongClickListener tvBillOnLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            transferAllItems();
            return true;
        }
    };

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
                wantsToPayOnKey();
            }
            return false;
        }
    };

    private TextView.OnEditorActionListener paysOnKeyListener = new TextView.OnEditorActionListener(){

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                paysOnKey();
            }
            return false;
        }
    };

    //////////////////////////////////////////// METHODS /////////////////////////////////////////////////////////////////////////////
    public void raiseChange(){
        updateListObjMainBillProduct();
        updateListObjMainCashBillProduct();
        setOpenTransitSum();
    }

    private void wantsToPayOnKey(){
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

    private void paysOnKey(){
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

    public void delTransitItems(String p_strCategory, String p_strProduct){
        if(m_iSessionTable != -1 && m_iSessionBill != -1) {
            for (ObjBillProduct objBillProduct : GlobVar.g_lstTables.get(m_iSessionTable).g_lstBills.get(getBillListPointer()).m_lstProducts) {
                if(objBillProduct.getCategory().equals(p_strCategory)){
                    if(objBillProduct.getProduct().getName().equals(p_strProduct)){
                        objBillProduct.setPayTransit(false);
                    }
                }
            }
        }
        raiseChange();
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

    private void setHeaderTable(){
        String strTableHeader = "";
         //if used as main cash register
        if(GlobVar.g_bUseMainCash){
            strTableHeader = getResources().getString(R.string.src_Hauptkasse);
            m_iSessionTable = 0;
        }
        else{
            strTableHeader = getResources().getString(R.string.src_Tisch) + " " + String.valueOf(m_iSessionTable+1);
        }
        m_TextViewTable.setText(strTableHeader);
    }

    private void setHeaderBill(){
        String strBillHeader = "";
        strBillHeader = getResources().getString(R.string.src_Beleg) + " " + String.valueOf(m_iSessionBill);
        m_TextViewBill.setText(strBillHeader);

        //set recyclerview
        initRecyclerViewMainBillProduct();     
    }

    private void transferAllItems(){
        if(m_iSessionTable != -1 && m_iSessionBill != -1){
            for(ObjBillProduct objBillProduct : GlobVar.g_lstTables.get(m_iSessionTable).g_lstBills.get(getBillListPointer()).m_lstProducts) {
                if ((!GlobVar.g_bUseMainCash && !objBillProduct.getPayTransit() && !objBillProduct.getPaid() && !objBillProduct.getCanceled() && !objBillProduct.getReturned() && objBillProduct.getPrinted())
                        || (GlobVar.g_bUseMainCash && GlobVar.g_bUseBonPrint && !objBillProduct.getPayTransit() && !objBillProduct.getPaid() && !objBillProduct.getCanceled() && !objBillProduct.getReturned() && objBillProduct.getPrinted())
                        || (GlobVar.g_bUseMainCash && !objBillProduct.getPayTransit() && !objBillProduct.getPaid() && !objBillProduct.getCanceled() && !objBillProduct.getReturned())) {
                    objBillProduct.setPayTransit(true);
                }
            }
        }
        //update view
        raiseChange();
    }

    public void transferAllProductItems(ObjMainBillProduct p_objMainCashProduct){
        if(m_iSessionTable != -1 && m_iSessionBill != -1){
            for(ObjBillProduct objBillProduct : GlobVar.g_lstTables.get(m_iSessionTable).g_lstBills.get(getBillListPointer()).m_lstProducts) {
                if ((!GlobVar.g_bUseMainCash && !objBillProduct.getPayTransit() && !objBillProduct.getPaid() && !objBillProduct.getCanceled() && !objBillProduct.getReturned() && objBillProduct.getPrinted())
                        || (GlobVar.g_bUseMainCash && GlobVar.g_bUseBonPrint && !objBillProduct.getPayTransit() && !objBillProduct.getPaid() && !objBillProduct.getCanceled() && !objBillProduct.getReturned() && objBillProduct.getPrinted())
                        || (GlobVar.g_bUseMainCash && !objBillProduct.getPayTransit() && !objBillProduct.getPaid() && !objBillProduct.getCanceled() && !objBillProduct.getReturned())){
                    if(objBillProduct.getProduct() == p_objMainCashProduct.getProduct()){
                        objBillProduct.setPayTransit(true);
                    }
                }
            }
        }
        //update view
        raiseChange();
    }

    public boolean isBillEmpty(int p_iBillNr){
        //get bill
        int iBill = 0;
        for(ObjBill objBill : GlobVar.g_lstTables.get(m_iSessionTable).g_lstBills){
            if(objBill.getBillNr() == p_iBillNr){
                break;
            }
            iBill++;
        }

        for(ObjBillProduct objBillProduct : GlobVar.g_lstTables.get(m_iSessionTable).g_lstBills.get(iBill).m_lstProducts){
            if(!objBillProduct.getCanceled() && !objBillProduct.getReturned()){
                if(!objBillProduct.getPaid()){
                    return false;
                }
            }
        }
        return true;
    }

    private void setOpenSum(){
        String strOpenSum;
        if(m_iSessionTable != -1 && m_iSessionBill != -1){
            double prize = 0.00;
            for(ObjBillProduct objBillProduct : GlobVar.g_lstTables.get(m_iSessionTable).g_lstBills.get(getBillListPointer()).m_lstProducts) {
                if(!objBillProduct.getPayTransit() && !objBillProduct.getPaid()
                        && !objBillProduct.getCanceled() && !objBillProduct.getReturned()){
                    prize += objBillProduct.getVK();

                    //if product has a pawn prize
                    if(objBillProduct.getProduct().getbPawn()){
                        prize += objBillProduct.getProduct().getPawn();
                    }
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
            for(ObjBillProduct objBillProduct : GlobVar.g_lstTables.get(m_iSessionTable).g_lstBills.get(getBillListPointer()).m_lstProducts) {
                if(objBillProduct.getPayTransit()){
                    prize += objBillProduct.getVK();

                    //if product has a pawn prize
                    if(objBillProduct.getProduct().getbPawn()){
                        prize += objBillProduct.getProduct().getPawn();
                    }
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
                m_dTip = m_dWantsToPay - m_dToPay;
            }
            else{
                dChange = m_dToPay - m_dPays;
                m_dTip = 0.0;
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
            m_dChange = dChange;
        }
    }

    private int getBillListPointer(){
        //get bill
        int iBill = 0;
        for(ObjBill objBill : GlobVar.g_lstTables.get(m_iSessionTable).g_lstBills){
            if(objBill.getBillNr() == m_iSessionBill){
                return iBill;
            }
            iBill++;
        }
        return 0;
    }

    private void setPayTransitFalse(){
        for(ObjBillProduct objBillProduct : GlobVar.g_lstTables.get(m_iSessionTable).g_lstBills.get(getBillListPointer()).m_lstProducts){
            objBillProduct.setPayTransit(false);
        }
    }

    private void startPayment(){
        if(m_ListObjMainCashBillProduct.size() > 0){
            if(m_dWantsToPay >= m_dToPay){
                if(m_dPays >= m_dWantsToPay){
                    FragmentManager fm = getSupportFragmentManager();
                    PopUpWindowMainCashPrintBillFragment popUpWindowMainCashPrintBillFragment = PopUpWindowMainCashPrintBillFragment.newInstance();

                    // pass text to fragment
                    Bundle args = new Bundle();
                    String strText = getResources().getString(R.string.src_BitteBezahlvorgangBestaetigen) + "\n\n";

                    DecimalFormat df = new DecimalFormat("0.00");
                    strText += getResources().getString(R.string.src_KundeBekommtSummeXZurueck);
                    String strVK = df.format(m_dChange);
                    strText = strText.replace("{0}", strVK);

                    args.putString("TEXT", strText);
                    args.putString("TASK", "pay");

                    popUpWindowMainCashPrintBillFragment.setArguments(args);
                    popUpWindowMainCashPrintBillFragment.show(fm, "fragment_popupprintbill");
                }
                else{
                    Toast.makeText(MainCash.this, getResources().getString(R.string.src_BitteSummeUeberpruefen), Toast.LENGTH_SHORT).show();
                }
            }
            else{
                if(m_dPays >= m_dToPay){
                    FragmentManager fm = getSupportFragmentManager();
                    PopUpWindowMainCashPrintBillFragment popUpWindowMainCashPrintBillFragment = PopUpWindowMainCashPrintBillFragment.newInstance();

                    // pass text to fragment
                    Bundle args = new Bundle();
                    String strText = getResources().getString(R.string.src_BitteBezahlvorgangBestaetigen) + "\n\n";

                    DecimalFormat df = new DecimalFormat("0.00");
                    strText += getResources().getString(R.string.src_KundeBekommtSummeXZurueck);
                    String strVK = df.format(m_dChange);
                    strText = strText.replace("{0}", strVK);

                    args.putString("TEXT", strText);
                    args.putString("TASK", "pay");

                    popUpWindowMainCashPrintBillFragment.setArguments(args);
                    popUpWindowMainCashPrintBillFragment.show(fm, "fragment_popupprintbill");
                }
                else{
                    Toast.makeText(MainCash.this, getResources().getString(R.string.src_BitteSummeUeberpruefen), Toast.LENGTH_SHORT).show();
                }
            }
        }
        else{
            Toast.makeText(MainCash.this, getResources().getString(R.string.src_KeineArtikelZumBezahlenAusgewaehlt), Toast.LENGTH_SHORT).show();
        }

    }

    private void setPaid(){
        for(ObjBillProduct objBillProduct : GlobVar.g_lstTables.get(m_iSessionTable).g_lstBills.get(getBillListPointer()).m_lstProducts){
            if(objBillProduct.getPayTransit()){
                objBillProduct.setPaid(true);
                objBillProduct.setSqlChanged(true);
            }
        }

        //set bill tip
        double dTip = GlobVar.g_lstTables.get(m_iSessionTable).g_lstBills.get(getBillListPointer()).getTip();
        dTip += m_dTip;
        GlobVar.g_lstTables.get(m_iSessionTable).g_lstBills.get(getBillListPointer()).setTip(dTip);
        GlobVar.g_lstTables.get(m_iSessionTable).g_lstBills.get(getBillListPointer()).setSqlChanged(true);

        //if bill is completely empty, then close it
        if (isBillEmpty(m_iSessionBillOLD)) {
            GlobVar.g_lstTables.get(m_iSessionTable).g_lstBills.get(getBillListPointer()).setClosed(true);
        }

        //update database
        SQLiteDatabaseHandler_TableBills db_tablebills = new SQLiteDatabaseHandler_TableBills(m_Context);
        db_tablebills.addTableBill(m_iSessionTable, m_iSessionBill);
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

    //left side
    private void initRecyclerViewMainBillProduct(){
        m_rv_adapter = new RecyclerViewMainCashBillAdapter(this, m_ListObjMainBillProduct);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        m_recyclerview.setLayoutManager(mLayoutManager);
        m_recyclerview.setAdapter(m_rv_adapter);

        updateListObjMainBillProduct();
        setInfoRecyclerViewMainBillProduct();
    }

    //left side
    private void updateListObjMainBillProduct(){
        //get global list
        List<ObjBillProduct> lstObjBillProduct = GlobVar.g_lstTables.get(m_iSessionTable).g_lstBills.get(getBillListPointer()).m_lstProducts;

        //set shown false
        for(ObjBillProduct objBillProduct : lstObjBillProduct) {
            objBillProduct.setShown(false);
        }

        //set list
        for(ObjBillProduct objBillProductAdapter : lstObjBillProduct){
            //add new item or update
            if((!GlobVar.g_bUseMainCash && objBillProductAdapter.getPrinted() && !objBillProductAdapter.getPayTransit() && !objBillProductAdapter.getPaid() && !objBillProductAdapter.getCanceled() && !objBillProductAdapter.getReturned() && !objBillProductAdapter.isShown())
                    || (GlobVar.g_bUseMainCash && GlobVar.g_bUseBonPrint && objBillProductAdapter.getPrinted() && !objBillProductAdapter.getPayTransit() && !objBillProductAdapter.getPaid() && !objBillProductAdapter.getCanceled() && !objBillProductAdapter.getReturned() && !objBillProductAdapter.isShown())
                    || (GlobVar.g_bUseMainCash && !objBillProductAdapter.getPayTransit() && !objBillProductAdapter.getPaid() && !objBillProductAdapter.getCanceled() && !objBillProductAdapter.getReturned() && !objBillProductAdapter.isShown())){
                //init variables
                ObjBillProduct objBillProductSearch = objBillProductAdapter;
                int iQuantity = 0;
                int iPrinted = 0;
                double dPrize = 0.0;
                boolean bFound = false;

                for(ObjBillProduct objBillProduct : lstObjBillProduct){
                    if(objBillProduct.getProduct() == objBillProductSearch.getProduct()){
                        if((!GlobVar.g_bUseMainCash && objBillProduct.getPrinted() && !objBillProduct.getPayTransit() && !objBillProduct.getPaid() && !objBillProduct.getCanceled() && !objBillProduct.getReturned() && !objBillProduct.isShown())
                                || (GlobVar.g_bUseMainCash && GlobVar.g_bUseBonPrint && objBillProduct.getPrinted() && !objBillProduct.getPayTransit() && !objBillProduct.getPaid() && !objBillProduct.getCanceled() && !objBillProduct.getReturned() && !objBillProduct.isShown())
                                || (GlobVar.g_bUseMainCash && !objBillProduct.getPayTransit() && !objBillProduct.getPaid() && !objBillProduct.getCanceled() && !objBillProduct.getReturned() && !objBillProduct.isShown())){
                            iQuantity++;
                            dPrize += objBillProduct.getVK();
                            //if pawn is available
                            if(objBillProduct.getProduct().getbPawn()){
                                dPrize += objBillProduct.getProduct().getPawn();
                            }

                            if(objBillProduct.getPrinted()){
                                iPrinted++;
                            }
                            objBillProduct.setShown(true);
                            bFound = true;
                        }
                    }
                }
                if(bFound){
                    boolean bExists = false;
                    ObjMainBillProduct objMainBillProductExists =  new ObjMainBillProduct();
                    for(ObjMainBillProduct objMainBillProduct : m_ListObjMainBillProduct){
                        if(objMainBillProduct.getProduct() == objBillProductSearch.getProduct()) {
                            objMainBillProductExists = objMainBillProduct;
                            bExists = true;
                            break;
                        }
                    }

                    //only update list
                    if(bExists){
                        objMainBillProductExists.setQuantity(iQuantity);
                        objMainBillProductExists.setPrinted(iPrinted);
                        objMainBillProductExists.setVK(dPrize);
                    }
                    //add new item
                    else{
                        ObjMainBillProduct objMainBillProduct = new ObjMainBillProduct();
                        objMainBillProduct.setProduct(objBillProductSearch.getProduct());
                        objMainBillProduct.setQuantity(iQuantity);
                        objMainBillProduct.setPrinted(iPrinted);
                        objMainBillProduct.setVK(dPrize);

                        this.m_ListObjMainBillProduct.add(objMainBillProduct);
                    }
                }
            }
        }

        //delete items
        for(int i = m_ListObjMainBillProduct.size(); i-- > 0;) {
            boolean bKeepAlive = false;
            for(ObjBillProduct objBillProductAdapter : lstObjBillProduct){
                if(objBillProductAdapter.getProduct() == m_ListObjMainBillProduct.get(i).getProduct()){
                    if((!GlobVar.g_bUseMainCash && !objBillProductAdapter.getPayTransit() && !objBillProductAdapter.getPaid() && !objBillProductAdapter.getCanceled() && !objBillProductAdapter.getReturned() && objBillProductAdapter.getPrinted())
                            || (GlobVar.g_bUseMainCash && GlobVar.g_bUseBonPrint && !objBillProductAdapter.getPayTransit() && !objBillProductAdapter.getPaid() && !objBillProductAdapter.getCanceled() && !objBillProductAdapter.getReturned() && objBillProductAdapter.getPrinted())
                            || (GlobVar.g_bUseMainCash && !objBillProductAdapter.getPayTransit() && !objBillProductAdapter.getPaid() && !objBillProductAdapter.getCanceled() && !objBillProductAdapter.getReturned())){
                        bKeepAlive = true;
                        break;
                    }
                }
            }
            if(!bKeepAlive){
                m_ListObjMainBillProduct.remove(i);
            }
        }

        //update recyclerview
        m_rv_adapter.notifyDataSetChanged();
        setInfoRecyclerViewMainBillProduct();
    }

    private void setInfoRecyclerViewMainBillProduct(){
        //get list bill pointer
        final int iBill = getBillListPointer();

        //set opensum
        setOpenSum();

        //set infotext mainbill
        if(GlobVar.g_lstTables.get(m_iSessionTable).g_lstBills.get(iBill).m_lstProducts.size() > 0){
            boolean bFound = false;
            for(ObjBillProduct objBillProduct : GlobVar.g_lstTables.get(m_iSessionTable).g_lstBills.get(iBill).m_lstProducts){
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

    //right side
    private void initListViewMainCashBill(){
        m_lv_adapter = new ListViewMainCashBillPayAdapter(m_Context, m_ListObjMainCashBillProduct);
        m_listview.setAdapter(m_lv_adapter);
    }

    //right side
    private void updateListObjMainCashBillProduct(){

        List<ObjBillProduct> lstObjBillProduct = GlobVar.g_lstTables.get(m_iSessionTable).g_lstBills.get(getBillListPointer()).m_lstProducts;

        //set shown false
        for(ObjBillProduct objBillProduct : lstObjBillProduct) {
            objBillProduct.setShown(false);
        }

        //set list
        for(ObjBillProduct objBillProductAdapter : lstObjBillProduct){
            //add new item or update
            if((!GlobVar.g_bUseMainCash && objBillProductAdapter.getPayTransit() && !objBillProductAdapter.getPaid() && !objBillProductAdapter.getCanceled()
                    && !objBillProductAdapter.getReturned() && !objBillProductAdapter.isShown() && objBillProductAdapter.getPrinted())
                    || (GlobVar.g_bUseMainCash && GlobVar.g_bUseBonPrint && objBillProductAdapter.getPayTransit() && !objBillProductAdapter.getPaid() && !objBillProductAdapter.getCanceled()
                    && !objBillProductAdapter.getReturned() && !objBillProductAdapter.isShown() && objBillProductAdapter.getPrinted())
                    || (GlobVar.g_bUseMainCash && objBillProductAdapter.getPayTransit() && !objBillProductAdapter.getPaid() && !objBillProductAdapter.getCanceled()
                    && !objBillProductAdapter.getReturned() && !objBillProductAdapter.isShown())){
                //init variables
                ObjBillProduct objBillProductSearch = objBillProductAdapter;
                int iQuantity = 0;
                double dPrize = 0.0;
                boolean bFound = false;

                for(ObjBillProduct objBillProduct : lstObjBillProduct){
                    if(objBillProduct.getProduct() == objBillProductSearch.getProduct()){
                        if((!GlobVar.g_bUseMainCash && objBillProduct.getPayTransit() && !objBillProduct.getPaid() && !objBillProduct.getCanceled()
                                && !objBillProduct.getReturned() && !objBillProduct.isShown() && objBillProductAdapter.getPrinted())
                                || (GlobVar.g_bUseMainCash && GlobVar.g_bUseBonPrint && objBillProduct.getPayTransit() && !objBillProduct.getPaid() && !objBillProduct.getCanceled()
                                && !objBillProduct.getReturned() && !objBillProduct.isShown() && objBillProductAdapter.getPrinted())
                                || (GlobVar.g_bUseMainCash && objBillProduct.getPayTransit() && !objBillProduct.getPaid() && !objBillProduct.getCanceled()
                                && !objBillProduct.getReturned() && !objBillProduct.isShown())){
                            iQuantity++;
                            dPrize += objBillProduct.getVK();
                            //if pawn is available
                            if(objBillProduct.getProduct().getbPawn()){
                                dPrize += objBillProduct.getProduct().getPawn();
                            }

                            objBillProduct.setShown(true);
                            bFound = true;
                        }
                    }
                }
                if(bFound){
                    boolean bExists = false;
                    ObjMainCashBillProduct objMainCashBillProductExists =  new ObjMainCashBillProduct();
                    for(ObjMainCashBillProduct objMainCashBillProduct : m_ListObjMainCashBillProduct){
                        if(objMainCashBillProduct.getProduct() == objBillProductSearch.getProduct()) {
                            objMainCashBillProductExists = objMainCashBillProduct;
                            bExists = true;
                            break;
                        }
                    }

                    //only update list
                    if(bExists){
                        objMainCashBillProductExists.setQuantity(iQuantity);
                        objMainCashBillProductExists.setSum(dPrize);
                    }
                    //add new item
                    else{
                        ObjMainCashBillProduct objMainBillProduct  = new ObjMainCashBillProduct();
                        objMainBillProduct.setProduct(objBillProductSearch.getProduct());
                        objMainBillProduct.setQuantity(iQuantity);
                        objMainBillProduct.setSum(dPrize);

                        this.m_ListObjMainCashBillProduct.add(objMainBillProduct);
                    }
                }
            }
        }

        //delete items
        for(int i = m_ListObjMainCashBillProduct.size(); i-- > 0;) {
            boolean bKeepAlive = false;
            for(ObjBillProduct objBillProductAdapter : lstObjBillProduct){
                if(objBillProductAdapter.getProduct() == m_ListObjMainCashBillProduct.get(i).getProduct()){
                    if(objBillProductAdapter.getPayTransit()){
                        bKeepAlive = true;
                        break;
                    }
                }
            }
            if(!bKeepAlive){
                m_ListObjMainCashBillProduct.remove(i);
            }
        }

        //update listview
        m_lv_adapter.notifyDataSetChanged();

        //update usermenu
        updateUserMenu();
    }

    private void updateUserMenu(){
        if(m_MenuItemSplitBill != null){
            if(m_ListObjMainCashBillProduct.size() > 0 && m_ListObjMainBillProduct.size() != 0 && !m_bBillSplit){
                m_MenuItemSplitBill.setEnabled(true);
            }
            else{
                m_MenuItemSplitBill.setEnabled(false);
            }
        }
    }

    private void splitBill(){
        //init session variables
        m_bBillSplit = true;
        m_MenuItemSplitBill.setEnabled(false);

        //set new bill
        ObjBill objBill = new ObjBill();
        objBill.setBillNr(GlobVar.g_iBillNr + 1);
        objBill.setCashierName(GlobVar.g_ObjSession.getCashierName());

        //add products to new bill and delete it from old bill
        //delete items
        for(int i = GlobVar.g_lstTables.get(m_iSessionTable).g_lstBills.get(getBillListPointer()).m_lstProducts.size(); i-- > 0;) {
            if(GlobVar.g_lstTables.get(m_iSessionTable).g_lstBills.get(getBillListPointer()).m_lstProducts.get(i).getPayTransit()){
                objBill.m_lstProducts.add(GlobVar.g_lstTables.get(m_iSessionTable).g_lstBills.get(getBillListPointer()).m_lstProducts.get(i));
                GlobVar.g_lstTables.get(m_iSessionTable).g_lstBills.get(getBillListPointer()).m_lstProducts.remove(i);
            }
        }

        String pattern = "dd/MM/yyyy HH:mm:ss";
        DateFormat df = new SimpleDateFormat(pattern);

        Date date = Calendar.getInstance().getTime();
        String todayAsString = df.format(date);
        objBill.setBillingDate(todayAsString);

        GlobVar.g_lstTables.get(m_iSessionTable).g_lstBills.add(objBill);

        //set bill number and header
        GlobVar.g_iBillNr++;
        m_iSessionBill = GlobVar.g_iBillNr;
        setHeaderBill();

        //update GUI - delete all items on the left side
        m_ListObjMainBillProduct.clear();
        updateListObjMainBillProduct();
    }
}
