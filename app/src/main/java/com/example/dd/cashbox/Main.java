package com.example.dd.cashbox;

import android.content.Context;
import android.content.Intent;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import SQLite.SQLiteDatabaseHandler_Category;
import SQLite.SQLiteDatabaseHandler_Product;
import adapter.ViewPagerAdapter;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

import SQLite.SQLiteDatabaseHandler_Printer;
import androidx.viewpager.widget.ViewPager;
import global.GlobVar;
import objects.ObjCategory;
import objects.ObjProduct;

public class Main extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Context m_Context;
    private int m_iSessionId = 0;
    private View m_decorView;
    private NavigationView m_navigationView;
    private DrawerLayout m_DrawerLayout;
    private TabLayout m_TabLayout;
    private ViewPager m_ViewPager;
    private ViewPagerAdapter m_ViewPagerAdapter;
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

        //init variables
        m_Context = this;
        m_decorView = getWindow().getDecorView();
        m_DrawerLayout = findViewById(R.id.drawer_layout);
        m_navigationView = findViewById(R.id.am_menu_nav_view);
        m_TabLayout = findViewById(R.id.am_register_tab);
        m_ViewPager = findViewById(R.id.am_register_viewpager);

        //set UI
        m_decorView.setSystemUiVisibility(m_uiOptions);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);

        //read database
        readSQLiteDB();

        //set Listener
        m_navigationView.setNavigationItemSelectedListener(this);

        //open Drawer
        if(m_iSessionId == 1){
            m_DrawerLayout.openDrawer(GravityCompat.START);
        }

        //set Tabulator
        setTabulator();
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

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void readSQLiteDB(){
        try{
            if(GlobVar.m_bReadSQL){
                //read printers
                SQLiteDatabaseHandler_Printer db_printer = new SQLiteDatabaseHandler_Printer(m_Context);
                if(GlobVar.m_lstPrinter.isEmpty()){
                    GlobVar.m_lstPrinter = db_printer.allPrinters();
                }

                //read categories
                SQLiteDatabaseHandler_Category db_category = new SQLiteDatabaseHandler_Category(m_Context);
                if(GlobVar.m_lstCategory.isEmpty()) {
                    GlobVar.m_lstCategory = db_category.allCategories();
                }

                //read products and add to specific category
                SQLiteDatabaseHandler_Product db_products = new SQLiteDatabaseHandler_Product(m_Context);
                int indexcounter = 0;
                for(ObjCategory objcategory : GlobVar.m_lstCategory){
                    List<ObjProduct> lstProduct = new ArrayList<ObjProduct>();
                    ObjCategory category = new ObjCategory();
                    category = objcategory;

                    lstProduct = db_products.allCategoryProducts(objcategory.getName());
                    category.setProductList(lstProduct);

                    GlobVar.m_lstCategory.set(indexcounter, category);
                    indexcounter++;
                }

                //database read only at start of app
                GlobVar.m_bReadSQL = false;
            }
        }
        catch(SQLiteException se){
            Log.e(getClass().getSimpleName(), "Could not create or open the database");
        }
    }

    private void setTabulator(){
        m_ViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), GlobVar.m_lstCategory);
        m_ViewPager.setAdapter(m_ViewPagerAdapter);
        m_TabLayout.setupWithViewPager(m_ViewPager);
        m_TabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
    }
}
