package com.example.dd.cashbox;

import android.content.Context;
import android.content.Intent;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import SQLite.SQLiteDatabaseHandler_Category;
import SQLite.SQLiteDatabaseHandler_Product;
import SQLite.SQLiteDatabaseHandler_Session;
import SQLite.SQLiteDatabaseHandler_TableBills;
import SQLite.SQLiteDatabaseHandler_Tables;
import adapter.RecyclerViewMainBillAdapter;
import adapter.ViewPagerAdapter;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import SQLite.SQLiteDatabaseHandler_Printer;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import fragments.RetoureStornoDialogFragment;
import fragments.ViewPagerRegisterFragment;
import global.GlobVar;
import objects.ObjBill;
import objects.ObjBillProduct;
import objects.ObjCategory;
import objects.ObjMainBillProduct;
import objects.ObjPrintJob;
import objects.ObjPrinter;
import objects.ObjProduct;
import printer.PrintJobQueue;

public class Main extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private Context m_Context;
    private int m_iSessionId = 0;
    private int m_iSessionTable = -1;
    private int m_iSessionBill = -1;
    private View m_decorView;
    private Menu m_Menu;
    private MenuItem m_MenuItemAllBills;
    private MenuItem m_MenuItemPrintBill;
    private MenuItem m_MenuItemTables;
    private NavigationView m_navigationView;
    private DrawerLayout m_DrawerLayout;
    private TabLayout m_TabLayout;
    private ViewPager m_ViewPager;
    private ViewPagerAdapter m_ViewPagerAdapter;
    private TextView m_TextViewTable;
    private TextView m_TextViewBill;
    private TextView m_TextViewOpenSum;
    private View m_ViewNoBill;
    private View m_ViewEmptyBill;
    private Button m_btnRegisterDel;
    private RecyclerViewMainBillAdapter m_rv_adapter;
    private List<ObjMainBillProduct> m_ListObjMainBillProduct = new ArrayList<>();
    private RecyclerView m_recyclerview;

    //fab buttons
    private FloatingActionButton m_fab_newbill;
    private FloatingActionButton m_fab_print;
    private FloatingActionButton m_fab_pay;
    private FloatingActionButton m_fab_main;
    private Animation m_animShowFabNewBill;
    private Animation m_animHideFabNewBill;
    private Animation m_animShowFabPrint;
    private Animation m_animHideFabPrint;
    private Animation m_animShowFabPay;
    private Animation m_animHideFabPay;
    private boolean m_Fab_Status = false;

    private int m_uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //activity variables
        m_iSessionId = getIntent().getIntExtra("EXTRA_SESSION_ID", 0);
        m_iSessionTable = GlobVar.g_iSessionTable;
        m_iSessionBill = GlobVar.g_iSessionBill;

        //init variables
        m_Context = this;
        m_decorView = getWindow().getDecorView();
        m_DrawerLayout = findViewById(R.id.drawer_layout);
        m_navigationView = findViewById(R.id.am_menu_nav_view);
        m_TabLayout = findViewById(R.id.am_register_tab);
        m_ViewPager = findViewById(R.id.am_register_viewpager);
        m_TextViewTable = findViewById(R.id.activity_main_bill_tvtable);
        m_TextViewBill = findViewById(R.id.activity_main_bill_tvbill);
        m_TextViewOpenSum = findViewById(R.id.activity_main_bill_tvbillsum);
        m_ViewEmptyBill = findViewById(R.id.activity_main_bill_rv_noitem);
        m_ViewNoBill = findViewById(R.id.activity_main_bill_rv_nobill);
        m_recyclerview = findViewById(R.id.activity_main_bill_rv);
        m_btnRegisterDel = findViewById(R.id.am_menu_btnCashBoxDelete);

        //init fab buttons
        m_fab_newbill  = findViewById(R.id.fab_layoutanimation_newbill);
        m_fab_print  = findViewById(R.id.fab_layoutanimation_print);
        m_fab_pay  = findViewById(R.id.fab_layoutanimation_pay);
        m_fab_main  = findViewById(R.id.activity_main_bill_fb);
        m_animShowFabNewBill = AnimationUtils.loadAnimation(getApplication(), R.anim.fab_newbill_show);
        m_animHideFabNewBill = AnimationUtils.loadAnimation(getApplication(), R.anim.fab1_newbill_hide);
        m_animShowFabPrint = AnimationUtils.loadAnimation(getApplication(), R.anim.fab_print_show);
        m_animHideFabPrint = AnimationUtils.loadAnimation(getApplication(), R.anim.fab2_print_hide);
        m_animShowFabPay = AnimationUtils.loadAnimation(getApplication(), R.anim.fab_pay_show);
        m_animHideFabPay = AnimationUtils.loadAnimation(getApplication(), R.anim.fab_pay_hide);

        //set UI
        m_decorView.setSystemUiVisibility(m_uiOptions);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        m_navigationView.bringToFront();

        //read database
        readSQLiteDB();

        //init adapter
        initRecyclerViewListObjMainBillProduct();

        //set Table/Bill Header
        setHeaderTable();
        if(m_iSessionBill == -2){
            createNewBill();
        }
        else{
            setHeaderBill();
        }

        //set main register
        setRegister();

        //set open drawer
        setOpenDrawer();

        //open Drawer
        if(m_iSessionId == 1){
            m_DrawerLayout.openDrawer(GravityCompat.START);
        }

        //set drawermenu header
        setDrawerMenuHeader();

        //set Tabulator
        setTabulator();

        //set open sum
        setOpenSum();

        //start printer queue
        if(!GlobVar.g_bPrintQueueStarted){
            PrintJobQueue.startPrintJobQueue();
            GlobVar.g_bPrintQueueStarted = true;
        }

        //set Listener
        m_navigationView.setNavigationItemSelectedListener(this);
        m_fab_main.setOnClickListener(fabMainOnClickListener);
        m_fab_newbill.setOnClickListener(fabNewBillOnClickListener);
        m_fab_print.setOnClickListener(fabPrintOnClickListener);
        m_fab_pay.setOnClickListener(fabPayOnClickListener);
        m_TextViewTable.setOnClickListener(tvTableOnClickListener);
        m_TextViewBill.setOnClickListener(tvBillOnClickListener);
        m_btnRegisterDel.setOnClickListener(this);
    }

    ///////////////////////////////////////// LISTENER /////////////////////////////////////////////////////////////////////////

    @Override
    public void onClick(View v){
        switch (v.getId()) {
            case R.id.am_menu_btnCashBoxDelete:
                //this.deleteDatabase("ProductsDB");
                //this.deleteDatabase("CategoriesDB");
                //this.deleteDatabase("PrintersDB");
                this.deleteDatabase("TableBillsDB");
                this.deleteDatabase("TablesDB");
                this.deleteDatabase("SessionDB");
                Toast.makeText(Main.this, getResources().getString(R.string.src_KasseWurdeVollstaendigGeloescht), Toast.LENGTH_SHORT).show();
                break;

            default:
                break;
        }
    }

    private View.OnClickListener fabMainOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (m_Fab_Status == false) {
                //Display FAB menu
                showFab();
                m_Fab_Status = true;
            } else {
                //Close FAB menu
                hideFab();
                m_Fab_Status = false;
            }
        }
    };

    private View.OnClickListener tvTableOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(GlobVar.g_iTables != -1) {
                Intent intent = new Intent(Main.this, MainShowTables.class);
                GlobVar.g_iSessionTable = m_iSessionTable;
                startActivity(intent);
            }
            else{
                Toast.makeText(Main.this, getResources().getString(R.string.src_KeineTischeVorhanden), Toast.LENGTH_SHORT).show();
            }
        }
    };
    private View.OnClickListener tvBillOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(m_iSessionTable != -1){
                if(GlobVar.g_lstTableBills.size() > 0 && GlobVar.g_lstTableBills.get(m_iSessionTable).size() > 0 && billsToShow())
                {
                    Intent intent = new Intent(Main.this, MainShowBills.class);
                    GlobVar.g_iSessionTable = m_iSessionTable;
                    GlobVar.g_iSessionBill = m_iSessionBill;
                    startActivity(intent);
                }
                else{
                    Toast.makeText(Main.this, getResources().getString(R.string.src_KeineBelegeVorhanden), Toast.LENGTH_SHORT).show();
                }
            }
            else{
                Toast.makeText(Main.this, getResources().getString(R.string.src_KeinTischAusgewaehlt), Toast.LENGTH_SHORT).show();
            }
        }
    };
    private View.OnClickListener fabNewBillOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            createNewBill();
        }
    };
    private View.OnClickListener fabPrintOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            addPrintJob();
        }
    };
    private View.OnClickListener fabPayOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startPayProcess();
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
                m_DrawerLayout.openDrawer(GravityCompat.START);
                return true;
                
            case R.id.main_usermenu_allbills:
                Intent intent = new Intent(Main.this, AllBills.class);
                GlobVar.g_iSessionTable = m_iSessionTable;
                GlobVar.g_iSessionBill = m_iSessionBill;
                startActivity(intent);
                return true;
                
            case R.id.main_usermenu_printbill:
                addPrintJobBill();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_usermenu, menu);

        //init menu variables
        m_Menu = menu;
        m_MenuItemAllBills = menu.findItem(R.id.main_usermenu_allbills);
        m_MenuItemPrintBill = menu.findItem(R.id.main_usermenu_printbill);
        m_MenuItemAllBills.setEnabled(true);
        m_MenuItemPrintBill.setEnabled(true);

        return true;
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        m_DrawerLayout= findViewById(R.id.drawer_layout);
        m_DrawerLayout.closeDrawer(GravityCompat.START);

        switch (item.getItemId()) {
            case R.id.nav_cashposition:
                startActivity(new Intent(this, MenuCashPosition.class));
                GlobVar.g_iSessionTable = m_iSessionTable;
                GlobVar.g_iSessionBill = m_iSessionBill;
                return true;

            case R.id.nav_artikel:
                startActivity(new Intent(this, EditCategory.class));
                GlobVar.g_iSessionTable = m_iSessionTable;
                GlobVar.g_iSessionBill = m_iSessionBill;
                return true;

            case R.id.nav_tische:
                startActivity(new Intent(this, EditTable.class));
                GlobVar.g_iSessionTable = m_iSessionTable;
                GlobVar.g_iSessionBill = m_iSessionBill;
                return true;

            case R.id.nav_einstellungen:
                startActivity(new Intent(this, MenuSettings.class));
                GlobVar.g_iSessionTable = m_iSessionTable;
                GlobVar.g_iSessionBill = m_iSessionBill;
                return true;

            case R.id.nav_hilfe:
                GlobVar.g_iSessionTable = m_iSessionTable;
                GlobVar.g_iSessionBill = m_iSessionBill;
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    ///////////////////////////////////////// METHODS /////////////////////////////////////////////////////////////////////////

    public int getVarTable(){
        return m_iSessionTable;
    }

    public int getVarBill(){
        return m_iSessionBill;
    }

    public void raiseNewProduct(){
        updateListObjMainBillProduct();
        setOpenSum();
    }

    private void readSQLiteDB(){
        try{
            if(GlobVar.g_bReadSQL){
                //read printers
                SQLiteDatabaseHandler_Printer db_printer = new SQLiteDatabaseHandler_Printer(m_Context);
                if(GlobVar.g_lstPrinter.isEmpty()){
                    GlobVar.g_lstPrinter = db_printer.allPrinters();
                }

                //read session --> comes from server database or start screen (has to be implemented)
                SQLiteDatabaseHandler_Session db_session = new SQLiteDatabaseHandler_Session(m_Context);
                db_session.getSession();

                GlobVar.g_ObjSession.setCashierName("Susi");
                GlobVar.g_ObjSession.setHostName("MV / ASV Mühlacker");
                GlobVar.g_ObjSession.setPartyName("Fisch trifft Musik");
                GlobVar.g_ObjSession.setPartyDate("02.06.2019");

                //read categories
                SQLiteDatabaseHandler_Category db_category = new SQLiteDatabaseHandler_Category(m_Context);
                if(GlobVar.g_lstCategory.isEmpty()) {
                    GlobVar.g_lstCategory = db_category.allCategories();
                }

                //read products and add to specific category
                SQLiteDatabaseHandler_Product db_products = new SQLiteDatabaseHandler_Product(m_Context);
                int indexcounter = 0;
                for(ObjCategory objcategory : GlobVar.g_lstCategory){
                    List<ObjProduct> lstProduct = new ArrayList<ObjProduct>();
                    ObjCategory category = new ObjCategory();
                    category = objcategory;

                    lstProduct = db_products.allCategoryProducts(objcategory.getName());
                    category.setProductList(lstProduct);

                    GlobVar.g_lstCategory.set(indexcounter, category);
                    indexcounter++;
                }

                //read tables
                SQLiteDatabaseHandler_Tables db_tables = new SQLiteDatabaseHandler_Tables(m_Context);
                //if used as main cash register
                if(GlobVar.g_bUseMainCash){
                    GlobVar.g_iTables = 1;
                }
                else {
                    GlobVar.g_iTables = db_tables.getTables();
                }

                //read tablebills
                SQLiteDatabaseHandler_TableBills db_tablebills = new SQLiteDatabaseHandler_TableBills(m_Context);
                if(GlobVar.g_lstTableBills.isEmpty()) {
                    db_tablebills.readAllTableBills();
                }

                //get highest bill nr
                int iBillNr = 0;
                for(List<ObjBill> lstObjBill : GlobVar.g_lstTableBills){
                    for(ObjBill objBill : lstObjBill){
                        if(objBill.getBillNr() > iBillNr){
                            iBillNr = objBill.getBillNr();
                        }
                    }
                }
                GlobVar.g_iBillNr = iBillNr;

                //database read only at start of app
                GlobVar.g_bReadSQL = false;
            }
        }
        catch(SQLiteException se){
            Log.e(getClass().getSimpleName(), "Could not create or open the database");
        }
    }

    private void setOpenDrawer(){
        Menu menuNav = m_navigationView.getMenu();
        m_MenuItemTables = menuNav.findItem(R.id.nav_tische);

        //if used as main cash register
        if(GlobVar.g_bUseMainCash){
            // disabled
            m_MenuItemTables.setEnabled(false);
            m_MenuItemTables.setVisible(false);
        } else {
            m_MenuItemTables.setEnabled(true);
            m_MenuItemTables.setVisible(true);
        }
    }

    private void setTabulator(){
        //setup viewpager
        m_ViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        int position = 0;
        for(ObjCategory objCategory : GlobVar.g_lstCategory){
            if(objCategory.getEnabled()){
                m_ViewPagerAdapter.addFragment(new ViewPagerRegisterFragment().getInstance(position), objCategory.getName(), objCategory.getProdColor(), m_Context);
                position++;
            }
        }
        m_ViewPager.setAdapter(m_ViewPagerAdapter);

        //setup custom view
        m_TabLayout.setupWithViewPager(m_ViewPager);
        m_TabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        View headerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.am_register_tablayout, null, false);

        //set for all tabs
        for(int tabs = 0; tabs < m_TabLayout.getTabCount(); tabs++){
            m_TabLayout.getTabAt(tabs).setCustomView(m_ViewPagerAdapter.getTabView(tabs));
        }

    }

    private void showFab(){

        //Floating Action Button 1
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) m_fab_newbill.getLayoutParams();
        layoutParams.leftMargin += (int) (m_fab_newbill.getWidth() * 1.7);
        layoutParams.bottomMargin += (int) (m_fab_newbill.getHeight() * 0.10);
        m_fab_newbill.setLayoutParams(layoutParams);
        m_fab_newbill.startAnimation(m_animShowFabNewBill);
        m_fab_newbill.setClickable(true);

        //Floating Action Button 2
        if(!GlobVar.g_bUseMainCash || ( GlobVar.g_bUseMainCash && GlobVar.g_bUseBonPrint)){
            FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) m_fab_print.getLayoutParams();
            layoutParams2.leftMargin += (int) (m_fab_print.getWidth() * 1.5);
            layoutParams2.bottomMargin += (int) (m_fab_print.getHeight() * 1.5);
            m_fab_print.setLayoutParams(layoutParams2);
            m_fab_print.startAnimation(m_animShowFabPrint);
        }


        //Floating Action Button 3
        FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) m_fab_pay.getLayoutParams();
        layoutParams3.leftMargin += (int) (m_fab_pay.getWidth() * 0.1);
        layoutParams3.bottomMargin += (int) (m_fab_pay.getHeight() * 1.7);
        m_fab_pay.setLayoutParams(layoutParams3);
        m_fab_pay.startAnimation(m_animShowFabPay);
        m_fab_pay.setClickable(true);
    }

    private void hideFab(){

        //Floating Action Button 1
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) m_fab_newbill.getLayoutParams();
        layoutParams.leftMargin -= (int) (m_fab_newbill.getWidth() * 1.7);
        layoutParams.bottomMargin -= (int) (m_fab_newbill.getHeight() * 0.10);
        m_fab_newbill.setLayoutParams(layoutParams);
        m_fab_newbill.startAnimation(m_animHideFabNewBill);
        m_fab_newbill.setClickable(false);

        //Floating Action Button 2
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) m_fab_print.getLayoutParams();
        layoutParams2.leftMargin -= (int) (m_fab_print.getWidth() * 1.5);
        layoutParams2.bottomMargin -= (int) (m_fab_print.getHeight() * 1.5);
        m_fab_print.setLayoutParams(layoutParams2);
        m_fab_print.startAnimation(m_animHideFabPrint);
        //if used as main cash register
        if(GlobVar.g_bUseMainCash  && !GlobVar.g_bUseBonPrint){
            m_fab_print.setClickable(false);
        }
        else{
            m_fab_print.setClickable(true);
        }

        //Floating Action Button 3
        FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) m_fab_pay.getLayoutParams();
        layoutParams3.leftMargin -= (int) (m_fab_pay.getWidth() * 0.1);
        layoutParams3.bottomMargin -= (int) (m_fab_pay.getHeight() * 1.7);
        m_fab_pay.setLayoutParams(layoutParams3);
        m_fab_pay.startAnimation(m_animHideFabPay);
        m_fab_pay.setClickable(false);
    }

    private void setRegister(){
        if(GlobVar.g_lstCategory.isEmpty()){
            findViewById(R.id.am_register_noarticle).setVisibility(View.VISIBLE);
        }
        else{
            findViewById(R.id.am_register_noarticle).setVisibility(View.INVISIBLE);
        }
    }

    private boolean addPrintJob(){
        if (m_iSessionTable != -1 && m_iSessionBill != -1) {
            //bill closed?
            if(!GlobVar.g_lstTableBills.get(m_iSessionTable).get(getBillListPointer()).getClosed()){
                boolean bPrinted = false;
                GlobVar.g_bPrintQueueFilling = true;

                //write bill to printqueue
                for(ObjCategory objCategory : GlobVar.g_lstCategory){
                    ObjPrinter objPrinter = objCategory.getPrinter();
                    if(objPrinter != null){
                        for(ObjProduct objProduct : objCategory.getListProduct()){
                            int iQuantity = 0;
                            for (ObjBillProduct objBillProduct : GlobVar.g_lstTableBills.get(m_iSessionTable).get(getBillListPointer()).m_lstProducts){
                                if(objProduct == objBillProduct.getProduct()){
                                    if(!objBillProduct.getPrinted() && !objBillProduct.getReturned() && !objBillProduct.getCanceled() && !objBillProduct.getPaid()){
                                        //if billproduct has no extra info
                                        if(objBillProduct.getAddInfo().equals("")){
                                            iQuantity++;
                                        }
                                        //if billproduct has extrainfo
                                        else{
                                            ObjPrintJob objPrintJob = new ObjPrintJob();
                                            objPrintJob.setContext(m_Context);
                                            objPrintJob.setPrinter(objPrinter);
                                            objPrintJob.setbBonExtra(true);

                                            String pattern = "dd/MM/yyyy HH:mm:ss";
                                            DateFormat df = new SimpleDateFormat(pattern);
                                            Date date = Calendar.getInstance().getTime();
                                            String todayAsString = df.format(date);

                                            //set bill text
                                            List<String> lstBillText = new ArrayList<>();
                                            lstBillText.add(GlobVar.g_ObjSession.getHostName());
                                            lstBillText.add(GlobVar.g_ObjSession.getPartyName() + " / " + GlobVar.g_ObjSession.getPartyDate());
                                            lstBillText.add(todayAsString);
                                            lstBillText.add("Tisch " + String.valueOf(m_iSessionTable + 1) + " - " + "Beleg " + String.valueOf(m_iSessionBill) + " - " + GlobVar.g_ObjSession.getCashierName());

                                            String strArticle = "1x " + objBillProduct.getProduct().getName();
                                            if(objBillProduct.getProduct().getbPawn()){
                                                strArticle += "*";
                                            }
                                            lstBillText.add(strArticle);
                                            lstBillText.add(objBillProduct.getAddInfo());
                                            objPrintJob.g_lstBillText = lstBillText;

                                            GlobVar.g_lstPrintJob.add(objPrintJob);
                                        }

                                        //print pawn bon
                                        if(objBillProduct.getProduct().getbPawn()){
                                            ObjPrintJob objPrintJob = new ObjPrintJob();
                                            objPrintJob.setContext(m_Context);
                                            objPrintJob.setPrinter(objPrinter);
                                            objPrintJob.setbBonPawn(true);

                                            String pattern = "dd/MM/yyyy HH:mm:ss";
                                            DateFormat df = new SimpleDateFormat(pattern);
                                            Date date = Calendar.getInstance().getTime();
                                            String todayAsString = df.format(date);

                                            //set bill text
                                            List<String> lstBillText = new ArrayList<>();
                                            lstBillText.add(GlobVar.g_ObjSession.getHostName());
                                            lstBillText.add(GlobVar.g_ObjSession.getPartyName() + " / " + GlobVar.g_ObjSession.getPartyDate());
                                            lstBillText.add(todayAsString);
                                            lstBillText.add("Tisch " + String.valueOf(m_iSessionTable + 1) + " - " + "Beleg " + String.valueOf(m_iSessionBill) + " - " + GlobVar.g_ObjSession.getCashierName());

                                            DecimalFormat dfprize = new DecimalFormat("0.00");
                                            String strPawn = "1x " + getResources().getString(R.string.src_Pfand);
                                            String strPawnPrize = dfprize.format(objBillProduct.getProduct().getPawn());
                                            strPawnPrize = strPawnPrize + "EUR";
                                            lstBillText.add(strPawn + " - " + strPawnPrize);

                                            lstBillText.add(objBillProduct.getProduct().getName());
                                            objPrintJob.g_lstBillText = lstBillText;

                                            GlobVar.g_lstPrintJob.add(objPrintJob);
                                        }
                                        //set printed
                                        objBillProduct.setPrinted(true);
                                        objBillProduct.setSqlChanged(true);
                                        bPrinted = true;
                                    }
                                }
                            }
                            if(iQuantity != 0){
                                ObjPrintJob objPrintJob = new ObjPrintJob();
                                objPrintJob.setContext(m_Context);
                                objPrintJob.setPrinter(objPrinter);
                                objPrintJob.setbBon(true);

                                String pattern = "dd/MM/yyyy HH:mm:ss";
                                DateFormat df = new SimpleDateFormat(pattern);
                                Date date = Calendar.getInstance().getTime();
                                String todayAsString = df.format(date);

                                //set bill text
                                List<String> lstBillText = new ArrayList<>();
                                lstBillText.add(GlobVar.g_ObjSession.getHostName());
                                lstBillText.add(GlobVar.g_ObjSession.getPartyName() + " / " + GlobVar.g_ObjSession.getPartyDate());
                                lstBillText.add(todayAsString);
                                lstBillText.add("Tisch " + String.valueOf(m_iSessionTable + 1) + " - " + "Beleg " + String.valueOf(m_iSessionBill) + " - " + GlobVar.g_ObjSession.getCashierName());

                                String strArticle = iQuantity + "x " + objProduct.getName();
                                if(objProduct.getbPawn()){
                                    strArticle += "*";
                                }
                                lstBillText.add(strArticle);
                                objPrintJob.g_lstBillText = lstBillText;

                                GlobVar.g_lstPrintJob.add(objPrintJob);
                            }
                        }
                    }
                    else{
                        Toast.makeText(Main.this, getResources().getString(R.string.src_KeineDruckerausgewaehlt), Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }

                //only change database and global list if items are printable
                if (bPrinted) {
                    //set print symbols
                    updateListObjMainBillProduct();
                }
                else {
                    Toast.makeText(Main.this, getResources().getString(R.string.src_EsWurdeBereitsAlleArtikelGedruckt), Toast.LENGTH_SHORT).show();
                }

                GlobVar.g_bPrintQueueFilling = false;
            }
            else{
                Toast.makeText(m_Context, getResources().getString(R.string.src_BelegBereitsGeschlossen), Toast.LENGTH_SHORT).show();
            }
        }
        else{
                Toast.makeText(Main.this, getResources().getString(R.string.src_KeinBelegAusgewaehlt), Toast.LENGTH_SHORT).show();
        }

        return true;
    }
    
    private void addPrintJobBill(){
        //TODO
        if (m_iSessionTable != -1 && m_iSessionBill != -1) {
            if (GlobVar.g_lstTableBills.get(m_iSessionTable).get(getBillListPointer()).m_lstProducts.size() > 0) {
                ObjBill objBill = GlobVar.g_lstTableBills.get(m_iSessionTable).get(getBillListPointer());
                boolean bPrinted = false;
                GlobVar.g_bPrintQueueFilling = true;

                ObjPrintJob objPrintJob = new ObjPrintJob();
                objPrintJob.setContext(m_Context);
                objPrintJob.setPrinter(GlobVar.g_objPrinter);
                objPrintJob.setbNormalBill(true);

                //set bill text
                List<String> lstBillText = new ArrayList<>();
                lstBillText.add(GlobVar.g_ObjSession.getHostName());
                lstBillText.add(GlobVar.g_ObjSession.getPartyName());
                lstBillText.add(GlobVar.g_ObjSession.getPartyDate());
                lstBillText.add(String.valueOf(objBill.getBillNr()));
                
                String str_Products = "";
                int iCount = 0;
                double dSum = 0.0;
                double dTax = 0.0;
                for(ObjBillProduct objBillProduct : objBill.m_lstProducts){
                    for(ObjBillProduct objBillProductTmp : objBill.m_lstProducts){
                        if(objBillProduct == objBillProductTmp && !objBillProductTmp.getPayTransit()){
                            //7% tax
                            if(objBillProductTmp.getProduct().getTax() == 7.0){
                                dSum += objBillProductTmp.getVK();
                                dTax = objBillProductTmp.getProduct().getTax();
                                
                                iCount++;
                                objBillProductTmp.setPayTransit(true);
                            }
                            //7% tax
                            else if(objBillProductTmp.getProduct().getTax() == 19.0){
                                dSum += objBillProductTmp.getVK();
                                dTax = objBillProductTmp.getProduct().getTax();
                                
                                iCount++;
                                objBillProductTmp.setPayTransit(true);
                            }
                            //togo
                            else {
                                dSum += objBillProductTmp.getVK();
                                dTax = objBillProductTmp.getProduct().getTax();
                                
                                iCount++;
                                objBillProductTmp.setPayTransit(true);
                            }
                        }
                    }
                    
                    if(iCount != 0){
                        DecimalFormat df = new DecimalFormat("0.00");
                        String strOutput = df.format(dSum) + "€";
                        
                        //19%
                        if(!objBillProduct.getToGo() || dTax == 19.0){
                            str_Products += iCount + "x " + objBillProduct.getProduct().getName() + " " + strOutput + "  A";
                        }
                        //7%
                        else{
                            str_Products += iCount + "x " + objBillProduct.getProduct().getName() + " " + strOutput + "  B";
                        }
                    }
                }
            }
            else{
            //TODO
            }
        }
    }                                             

    public void createNewBill(){
        if(m_iSessionTable != -1) {
            //init main cash register with one table
            if(GlobVar.g_bUseMainCash){
                if(GlobVar.g_lstTableBills.isEmpty()){
                    List<ObjBill> lstBill = new ArrayList<ObjBill>();
                    GlobVar.g_lstTableBills.add(lstBill);
                }
            }

            //only add new bill if last one is not empty
            if (m_iSessionBill != -1) {
                if (GlobVar.g_lstTableBills.get(m_iSessionTable).get(getBillListPointer()).m_lstProducts.size() > 0) {
                    //set new bill
                    ObjBill objBill = new ObjBill();
                    objBill.setBillNr(GlobVar.g_iBillNr + 1);
                    objBill.setTable(m_iSessionTable + 1);
                    objBill.setCashierName(GlobVar.g_ObjSession.getCashierName());

                    String pattern = "dd/MM/yyyy HH:mm:ss";
                    DateFormat df = new SimpleDateFormat(pattern);

                    Date date = Calendar.getInstance().getTime();
                    String todayAsString = df.format(date);
                    objBill.setBillingDate(todayAsString);

                    GlobVar.g_lstTableBills.get(m_iSessionTable).add(objBill);

                    //set bill number and header
                    //SimpleDateFormat dt = new SimpleDateFormat("yyyyymmddhhmm");
                    //int iDate = Integer.parseInt(dt.format(date));
                    GlobVar.g_iBillNr++;
                    m_iSessionBill = GlobVar.g_iBillNr;
                    setHeaderBill();
                    setOpenSum();

                    Toast.makeText(Main.this, getResources().getString(R.string.src_NeuerBelegHinzugefuegt), Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(Main.this, getResources().getString(R.string.src_NeuerBelegBereitsVorhanden), Toast.LENGTH_SHORT).show();
                }
            }
            else {
                //set new bill
                ObjBill objBill = new ObjBill();
                objBill.setBillNr(GlobVar.g_iBillNr + 1);
                objBill.setTable(m_iSessionTable + 1);
                objBill.setCashierName(GlobVar.g_ObjSession.getCashierName());

                String pattern = "dd/MM/yyyy HH:mm:ss";
                DateFormat df = new SimpleDateFormat(pattern);

                Date date = Calendar.getInstance().getTime();
                String todayAsString = df.format(date);
                objBill.setBillingDate(todayAsString);

                GlobVar.g_lstTableBills.get(m_iSessionTable).add(objBill);

                //set bill number and header
                //SimpleDateFormat dt = new SimpleDateFormat("yyyyymmddhhmm");
                //int iDate = Integer.parseInt(dt.format(date));
                GlobVar.g_iBillNr++;
                m_iSessionBill = GlobVar.g_iBillNr;
                setHeaderBill();
                setOpenSum();

                Toast.makeText(Main.this, getResources().getString(R.string.src_NeuerBelegHinzugefuegt), Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(Main.this, getResources().getString(R.string.src_KeinTischAusgewaehlt), Toast.LENGTH_SHORT).show();
        }
    }

    private void startPayProcess(){
        if (m_iSessionTable != -1 && m_iSessionBill != -1) {
            //bill closed?
            if(!GlobVar.g_lstTableBills.get(m_iSessionTable).get(getBillListPointer()).getClosed()){
                if(GlobVar.g_bUseMainCash){
                    if(m_iSessionBill != -1){
                        Intent intent = new Intent(Main.this, MainCash.class);
                        GlobVar.g_iSessionTable = 0;
                        GlobVar.g_iSessionBill = m_iSessionBill;
                        startActivity(intent);
                    }
                    else{
                        Toast.makeText(Main.this, getResources().getString(R.string.src_KeinBelegAusgewaehlt), Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    boolean bFound = false;
                    boolean bPrinted = true;
                    if (GlobVar.g_lstTableBills.get(m_iSessionTable).get(getBillListPointer()).m_lstProducts.size() > 0) {
                        for (ObjBillProduct objBillProduct : GlobVar.g_lstTableBills.get(m_iSessionTable).get(getBillListPointer()).m_lstProducts) {
                            if (objBillProduct.getPrinted() && !objBillProduct.getPaid()
                                    && !objBillProduct.getCanceled() && !objBillProduct.getReturned()) {
                                bFound = true;
                                break;
                            } else if (!objBillProduct.getPrinted() && !objBillProduct.getPaid()
                                    && !objBillProduct.getCanceled() && !objBillProduct.getReturned()) {
                                bPrinted = false;
                            }
                        }
                    }
                    if (bFound) {
                        Intent intent = new Intent(Main.this, MainCash.class);
                        GlobVar.g_iSessionTable = m_iSessionTable;
                        GlobVar.g_iSessionBill = m_iSessionBill;
                        startActivity(intent);
                    } else {
                        if (!bPrinted) {
                            Toast.makeText(Main.this, getResources().getString(R.string.src_EsWurdenNochKeineArtikelGedruckt), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Main.this, getResources().getString(R.string.src_KeineArtikelVorhanden), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
            else {
                Toast.makeText(m_Context, getResources().getString(R.string.src_BelegBereitsGeschlossen), Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(Main.this, getResources().getString(R.string.src_KeinBelegAusgewaehlt), Toast.LENGTH_SHORT).show();
        }
    }

    private void setDrawerMenuHeader(){
        View headerView = m_navigationView.getHeaderView(0);
        TextView tv_cashiername = headerView.findViewById(R.id.drawerheadercashiername);
        TextView tv_hostname = headerView.findViewById(R.id.drawerheaderhostname);
        TextView tv_partyname = headerView.findViewById(R.id.drawerheaderpartyname);

        tv_cashiername.setText(GlobVar.g_ObjSession.getCashierName());
        tv_hostname.setText(GlobVar.g_ObjSession.getHostName());
        tv_partyname.setText(GlobVar.g_ObjSession.getPartyName() + " - " + GlobVar.g_ObjSession.getPartyDate());
    }

    private void setHeaderTable(){
        String strTableHeader = "";
         //if used as main cash register
        if(GlobVar.g_bUseMainCash){
            strTableHeader = getResources().getString(R.string.src_Hauptkasse);
            m_TextViewTable.setEnabled(false);

            //init main cash register with one table
            m_iSessionTable = 0;
        }
        else{
            if(m_iSessionTable != -1){
                strTableHeader = getResources().getString(R.string.src_Tisch) + " " + String.valueOf(m_iSessionTable+1);
            }

            else{
                strTableHeader = getResources().getString(R.string.src_Tisch_emtpy);
            }
        }    
        m_TextViewTable.setText(strTableHeader);
    }

    private void setHeaderBill(){
        String strBillHeader = "";
        if(m_iSessionBill != -1){

            strBillHeader = getResources().getString(R.string.src_Beleg) + " " + String.valueOf(m_iSessionBill);

            //only set recyclerview when bill product list not empty
            if(GlobVar.g_lstTableBills.get(m_iSessionTable).get(getBillListPointer()).m_lstProducts == null){
                GlobVar.g_lstTableBills.get(m_iSessionTable).get(getBillListPointer()).m_lstProducts = new ArrayList<ObjBillProduct>();
            }
            //set recyclerview
            updateListObjMainBillProduct();
        }
        else{
            strBillHeader = getResources().getString(R.string.src_Beleg_empty);

            //set infotext mainbill
            m_ViewNoBill.setVisibility(View.VISIBLE);
            m_ViewEmptyBill.setVisibility(View.INVISIBLE);
        }
        m_TextViewBill.setText(strBillHeader);
    }

    private void setOpenSum(){
        String strOpenSum;
        if(m_iSessionTable != -1 && m_iSessionBill != -1){
            double prize = 0.00;
            for(ObjBillProduct objBillProduct : GlobVar.g_lstTableBills.get(m_iSessionTable).get(getBillListPointer()).m_lstProducts) {
                if(!objBillProduct.getPaid() && !objBillProduct.getCanceled() && !objBillProduct.getReturned()){
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

    private void initRecyclerViewListObjMainBillProduct(){
        m_rv_adapter = new RecyclerViewMainBillAdapter(this, m_ListObjMainBillProduct);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        m_recyclerview.setLayoutManager(mLayoutManager);
        m_recyclerview.setAdapter(m_rv_adapter);

        //updateListObjMainBillProduct();
        //setInfoRecyclerViewListObjMainBillProduct();
    }
    private void updateListObjMainBillProduct(){
        //write tablebills to database
        SQLiteDatabaseHandler_TableBills db_tablebills = new SQLiteDatabaseHandler_TableBills(m_Context);
        db_tablebills.addTableBill(m_iSessionTable, m_iSessionBill);

        //get global list
        List<ObjBillProduct> lstObjBillProduct = GlobVar.g_lstTableBills.get(m_iSessionTable).get(getBillListPointer()).m_lstProducts;

        //set shown false
        for(ObjBillProduct objBillProduct : lstObjBillProduct) {
            objBillProduct.setShown(false);
        }

        //set list
        for(ObjBillProduct objBillProductAdapter : lstObjBillProduct){
            //add new item or update list
            if(!objBillProductAdapter.isShown()){
                //init variables
                ObjBillProduct objBillProductSearch = objBillProductAdapter;
                int iQuantity = 0;
                int iPaid = 0;
                int iPrinted = 0;
                int iCanceled = 0;
                int iReturned = 0;
                double dPrize = 0.0;
                boolean bFound = false;

                for(ObjBillProduct objBillProduct : lstObjBillProduct){
                    if(objBillProduct.getProduct() == objBillProductSearch.getProduct()){
                        if(!objBillProduct.isShown()){
                            iQuantity++;

                            //show unpaid sum
                            if(!objBillProduct.getPaid() && !objBillProduct.getReturned() && !objBillProduct.getCanceled()) {
                                dPrize += objBillProduct.getVK();
                            }

                            //if pawn is available
                            if(objBillProduct.getProduct().getbPawn()){
                                dPrize += objBillProduct.getProduct().getPawn();
                            }

                            if(objBillProduct.getPaid() && !objBillProduct.getReturned()
                                && !objBillProduct.getCanceled()){
                                iPaid++;
                            }

                            if(objBillProduct.getPrinted()){
                                iPrinted++;
                            }

                            if(objBillProduct.getCanceled()){
                                iCanceled++;
                            }

                            if(objBillProduct.getReturned()){
                                iReturned++;
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
                        objMainBillProductExists.setPaid(iPaid);
                        objMainBillProductExists.setPrinted(iPrinted);
                        objMainBillProductExists.setCanceled(iCanceled);
                        objMainBillProductExists.setReturned(iReturned);
                        objMainBillProductExists.setVK(dPrize);
                    }
                    //add new item
                    else{
                        ObjMainBillProduct objMainBillProduct  = new ObjMainBillProduct();
                        objMainBillProduct.setProduct(objBillProductSearch.getProduct());
                        objMainBillProduct.setQuantity(iQuantity);
                        objMainBillProduct.setPaid(iPaid);
                        objMainBillProduct.setPrinted(iPrinted);
                        objMainBillProduct.setCanceled(iCanceled);
                        objMainBillProduct.setReturned(iReturned);
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
                    //if(!objBillProductAdapter.getCanceled()
                      //      && !objBillProductAdapter.getReturned()){
                        bKeepAlive = true;
                        break;
                   // }
                }
            }
            if(!bKeepAlive){
                m_ListObjMainBillProduct.remove(i);
            }
        }

        //update recyclerview
        m_rv_adapter.notifyDataSetChanged();
        setInfoRecyclerViewListObjMainBillProduct();
    }
    private void setInfoRecyclerViewListObjMainBillProduct(){
        //get list bill pointer
        final int iBill = getBillListPointer();

        //set infotext mainbill
        if(GlobVar.g_lstTableBills.get(m_iSessionTable).get(iBill).m_lstProducts.size() > 0){
            boolean bFound = false;
            for(ObjBillProduct objBillProduct : GlobVar.g_lstTableBills.get(m_iSessionTable).get(iBill).m_lstProducts){
                if((!objBillProduct.getPaid() && !objBillProduct.getCanceled() && !objBillProduct.getReturned())
                    || GlobVar.g_lstTableBills.get(m_iSessionTable).get(getBillListPointer()).getClosed() ){
                    bFound = true;
                    break;
                }
            }
            if(bFound){
                m_ViewNoBill.setVisibility(View.INVISIBLE);
                m_ViewEmptyBill.setVisibility(View.INVISIBLE);
            }
            else{
                m_ViewNoBill.setVisibility(View.INVISIBLE);
                m_ViewEmptyBill.setVisibility(View.VISIBLE);
            }
        }
        else{
            m_ViewNoBill.setVisibility(View.INVISIBLE);
            m_ViewEmptyBill.setVisibility(View.VISIBLE);
        }
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

    public void showRetoureStornoDialog(String p_strCategory, String p_strProduct) {
        FragmentManager fm = getSupportFragmentManager();
        RetoureStornoDialogFragment retoureStornoDialogFragment = RetoureStornoDialogFragment.newInstance();

        // pass table, bill to fragment
        Bundle args = new Bundle();
        args.putString("CATEGORY", p_strCategory);
        args.putString("PRODUCT", p_strProduct);
        args.putInt("TABLE", m_iSessionTable);
        args.putInt("BILL", m_iSessionBill);

        retoureStornoDialogFragment.setArguments(args);
        retoureStornoDialogFragment.show(fm, "fragment_retourestorno");
    }

    private boolean billsToShow(){
        for(ObjBill objBill : GlobVar.g_lstTableBills.get(m_iSessionTable)){
            for(ObjBillProduct objBillProduct : objBill.m_lstProducts){
                if(!objBillProduct.getPaid() && !objBillProduct.getCanceled() && !objBillProduct.getReturned()){
                    return true;
                }
            }
        }
        return false;
    }
}
