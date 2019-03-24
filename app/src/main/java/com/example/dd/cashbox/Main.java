package com.example.dd.cashbox;

import android.content.Context;
import android.content.Intent;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import SQLite.SQLiteDatabaseHandler_Category;
import SQLite.SQLiteDatabaseHandler_Product;
import SQLite.SQLiteDatabaseHandler_TableBills;
import SQLite.SQLiteDatabaseHandler_Tables;
import adapter.RecyclerViewCategoryAdapter;
import adapter.RecyclerViewMainBillAdapter;
import adapter.ViewPagerAdapter;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.database.sqlite.SQLiteException;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import SQLite.SQLiteDatabaseHandler_Printer;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import fragments.ViewPagerRegisterFragment;
import global.GlobVar;
import objects.ObjBill;
import objects.ObjBillProduct;
import objects.ObjCategory;
import objects.ObjPrinter;
import objects.ObjProduct;
import recyclerview.RecyclerItemTouchHelper;
import recyclerview.RecyclerItemTouchHelperActions;
import recyclerview.RecyclerItemTouchHelperBill;

public class Main extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Context m_Context;
    private int m_iSessionId = 0;
    private int m_iSessionTable = -1;
    private int m_iSessionBill = -1;
    private View m_decorView;
    private NavigationView m_navigationView;
    private DrawerLayout m_DrawerLayout;
    private TabLayout m_TabLayout;
    private ViewPager m_ViewPager;
    private ViewPagerAdapter m_ViewPagerAdapter;
    private TextView m_TextViewTable;
    private TextView m_TextViewBill;
    private RecyclerViewMainBillAdapter m_rv_adapter;
    private RecyclerItemTouchHelperBill m_RecyclerItemTouchHelper;
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
        m_iSessionTable = getIntent().getIntExtra("TABLE", -1);
        m_iSessionBill = getIntent().getIntExtra("BILL", 0);

        //init variables
        m_Context = this;
        m_decorView = getWindow().getDecorView();
        m_DrawerLayout = findViewById(R.id.drawer_layout);
        m_navigationView = findViewById(R.id.am_menu_nav_view);
        m_TabLayout = findViewById(R.id.am_register_tab);
        m_ViewPager = findViewById(R.id.am_register_viewpager);
        m_TextViewTable = findViewById(R.id.activity_main_bill_tvtable);
        m_TextViewBill = findViewById(R.id.activity_main_bill_tvbill);
        m_recyclerview = findViewById(R.id.activity_main_bill_rv);

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

        //set Table/Bill Header
        setHeaderTable();
        setHeaderBill();

        //set main register
        setRegister();

        //set Listener
        m_navigationView.setNavigationItemSelectedListener(this);
        m_fab_main.setOnClickListener(fabMainOnClickListener);
        m_fab_newbill.setOnClickListener(fabNewBillOnClickListener);
        m_fab_print.setOnClickListener(fabPrintOnClickListener);
        m_fab_pay.setOnClickListener(fabPayOnClickListener);
        m_TextViewTable.setOnClickListener(tvTableOnClickListener);
        m_TextViewBill.setOnClickListener(tvBillOnClickListener);

        //open Drawer
        if(m_iSessionId == 1){
            m_DrawerLayout.openDrawer(GravityCompat.START);
        }

        //set Tabulator
        setTabulator();
    }

    public int getVarTable(){
        return m_iSessionTable;
    }
    public int getVarBill(){
       return m_iSessionBill;
    }

    public void raiseNewProduct(){
        setupRecyclerView();
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
                startActivity(new Intent(m_Context, MainShowTables.class));
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
                if(GlobVar.g_lstTableBills.get(m_iSessionTable).size() > 0)
                {
                    Intent intent = new Intent(Main.this, MainShowBills.class);
                    intent.putExtra("TABLE", m_iSessionTable);
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
            if(m_iSessionTable != -1) {
                //set new bill
                ObjBill objBill = new ObjBill();
                objBill.setBillNr(GlobVar.g_iBillNr +1);
                objBill.setCashierName(GlobVar.g_strBedienername);

                Date date = new Date();
                objBill.setBillingDate(date);

                GlobVar.g_lstTableBills.get(m_iSessionTable).add(objBill);

                //set bill header
                GlobVar.g_iBillNr++;
                m_iSessionBill = GlobVar.g_iBillNr;
                setHeaderBill();

                Toast.makeText(Main.this, getResources().getString(R.string.src_NeuerBelegHinzugefuegt), Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(Main.this, getResources().getString(R.string.src_KeinTischAusgewaehlt), Toast.LENGTH_SHORT).show();
            }
        }
    };
    private View.OnClickListener fabPrintOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //set products as printed
            for(ObjBillProduct objBillProduct : GlobVar.g_lstTableBills.get(m_iSessionTable).get(getBillListPointer()).m_lstProducts){
                  objBillProduct.setPrinted(objBillProduct.getQuantity());
            }

            //set print symbols
            setupRecyclerView();

            //write tablebills to database
            SQLiteDatabaseHandler_TableBills db_tablebills = new SQLiteDatabaseHandler_TableBills(m_Context);
            db_tablebills.addTableBill(m_iSessionTable, m_iSessionBill);

        }
    };
    private View.OnClickListener fabPayOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

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
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        m_DrawerLayout= findViewById(R.id.drawer_layout);
        m_DrawerLayout.closeDrawer(GravityCompat.START);

        switch (item.getItemId()) {
            case R.id.nav_einstellungen:
                startActivity(new Intent(this, MenuSettings.class));
                return true;

            case R.id.nav_tische:
                startActivity(new Intent(this, EditTable.class));
                return true;

            case R.id.nav_artikel:
                startActivity(new Intent(this, EditCategory.class));
                return true;

            case R.id.nav_hilfe:
                /*this.deleteDatabase("ProductsDB");
                this.deleteDatabase("CategoriesDB");
                this.deleteDatabase("PrintersDB");*/
                this.deleteDatabase("TableBillsDB");
                this.deleteDatabase("TablesDB");
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void readSQLiteDB(){
        try{
            if(GlobVar.g_bReadSQL){
                //read printers
                SQLiteDatabaseHandler_Printer db_printer = new SQLiteDatabaseHandler_Printer(m_Context);
                if(GlobVar.g_lstPrinter.isEmpty()){
                    GlobVar.g_lstPrinter = db_printer.allPrinters();
                }

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
                GlobVar.g_iTables = db_tables.getTables();

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
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) m_fab_print.getLayoutParams();
        layoutParams2.leftMargin += (int) (m_fab_print.getWidth() * 1.5);
        layoutParams2.bottomMargin += (int) (m_fab_print.getHeight() * 1.5);
        m_fab_print.setLayoutParams(layoutParams2);
        m_fab_print.startAnimation(m_animShowFabPrint);
        m_fab_print.setClickable(true);

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
        m_fab_print.setClickable(false);

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

    private void setHeaderTable(){
        String strTableHeader = "";
        if(m_iSessionTable != -1){
            strTableHeader = getResources().getString(R.string.src_Tisch) + " " + String.valueOf(m_iSessionTable+1);
        }
        else{
            strTableHeader = getResources().getString(R.string.src_Tisch_emtpy);
        }
        m_TextViewTable.setText(strTableHeader);
    }

    private void setHeaderBill(){
        String strBillHeader = "";
        if(m_iSessionBill != 0){
            strBillHeader = getResources().getString(R.string.src_Beleg) + " " + String.valueOf(m_iSessionBill);

            //only set recyclerview when bill product list not empty
            if(GlobVar.g_lstTableBills.get(m_iSessionTable).get(getBillListPointer()).m_lstProducts == null){
                GlobVar.g_lstTableBills.get(m_iSessionTable).get(getBillListPointer()).m_lstProducts = new ArrayList<ObjBillProduct>();
            }
            //set recyclerview
            setupRecyclerView();
        }
        else{
            strBillHeader = getResources().getString(R.string.src_Beleg_empty);
        }
        m_TextViewBill.setText(strBillHeader);
    }
    private void setupRecyclerView(){

        //get list bill pointer
        int iBill = getBillListPointer();

        m_rv_adapter = new RecyclerViewMainBillAdapter(this, GlobVar.g_lstTableBills.get(m_iSessionTable).get(iBill).m_lstProducts);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        m_recyclerview.setLayoutManager(mLayoutManager);

        m_RecyclerItemTouchHelper = new RecyclerItemTouchHelperBill(new RecyclerItemTouchHelperActions() {
            @Override
            public void onRightClicked(int position) {
                //get current category
                //final ObjBillProduct objbillproduct = GlobVar.g_lstCategory.get(position);

                // backup of removed item for undo purpose
                final ObjCategory deletedItem = GlobVar.g_lstCategory.get(position);
                final int deletedIndex = position;

                m_rv_adapter.removeItem(position);
                m_rv_adapter.notifyItemRemoved(position);
                m_rv_adapter.notifyItemRangeChanged(position, m_rv_adapter.getItemCount());

                //delete category in database
                final SQLiteDatabaseHandler_Category db = new SQLiteDatabaseHandler_Category(m_Context);
                //db.deleteCategory(category);

                //delete all products of category
                final SQLiteDatabaseHandler_Product db_products = new SQLiteDatabaseHandler_Product(m_Context);
                //db_products.deleteProductsCategory(category.getName());

            }
        });

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(m_RecyclerItemTouchHelper);
        itemTouchhelper.attachToRecyclerView(m_recyclerview);

        m_recyclerview.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                m_RecyclerItemTouchHelper.onDraw(c);
            }
        });

        m_recyclerview.setAdapter(m_rv_adapter);
        m_rv_adapter.notifyDataSetChanged();
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
}
