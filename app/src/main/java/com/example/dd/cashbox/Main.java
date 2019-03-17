package com.example.dd.cashbox;

import android.content.Context;
import android.content.Intent;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

import SQLite.SQLiteDatabaseHandler_Printer;
import androidx.viewpager.widget.ViewPager;
import fragments.ViewPagerRegisterFragment;
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

    //fab buttons
    private FloatingActionButton m_fab_1;
    private FloatingActionButton m_fab_2;
    private FloatingActionButton m_fab_3;
    private FloatingActionButton m_fab_main;
    private Animation m_animShowFab1;
    private Animation m_animHideFab1;
    private Animation m_animShowFab2;
    private Animation m_animHideFab2;
    private Animation m_animShowFab3;
    private Animation m_animHideFab3;
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

        //init variables
        m_Context = this;
        m_decorView = getWindow().getDecorView();
        m_DrawerLayout = findViewById(R.id.drawer_layout);
        m_navigationView = findViewById(R.id.am_menu_nav_view);
        m_TabLayout = findViewById(R.id.am_register_tab);
        m_ViewPager = findViewById(R.id.am_register_viewpager);

        //init fab buttons
        m_fab_1  = findViewById(R.id.fab_layoutanimation_1);
        m_fab_2  = findViewById(R.id.fab_layoutanimation_2);
        m_fab_3  = findViewById(R.id.fab_layoutanimation_3);
        m_fab_main  = findViewById(R.id.activity_main_bill_fb);
        m_animShowFab1 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab1_show);
        m_animHideFab1 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab1_hide);
        m_animShowFab2 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab2_show);
        m_animHideFab2 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab2_hide);
        m_animShowFab3 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab3_show);
        m_animHideFab3 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab3_hide);

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
        m_fab_main.setOnClickListener(fabMainOnClickListener);

        //open Drawer
        if(m_iSessionId == 1){
            m_DrawerLayout.openDrawer(GravityCompat.START);
        }

        //set Tabulator
        setTabulator();
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

    private void showFab(){

        //Floating Action Button 1
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) m_fab_1.getLayoutParams();
        layoutParams.leftMargin += (int) (m_fab_1.getWidth() * 1.7);
        layoutParams.bottomMargin += (int) (m_fab_1.getHeight() * 0.25);
        m_fab_1.setLayoutParams(layoutParams);
        m_fab_1.startAnimation(m_animShowFab1);
        m_fab_1.setClickable(true);

        /*//Floating Action Button 2
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) m_fab_2.getLayoutParams();
        layoutParams2.leftMargin += (int) (m_fab_2.getWidth() * 1.5);
        layoutParams2.bottomMargin += (int) (m_fab_2.getHeight() * 1.5);
        m_fab_2.setLayoutParams(layoutParams2);
        m_fab_2.startAnimation(m_animShowFab2);
        m_fab_2.setClickable(true);

        //Floating Action Button 3
        FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) m_fab_3.getLayoutParams();
        layoutParams3.leftMargin += (int) (m_fab_3.getWidth() * 0.25);
        layoutParams3.bottomMargin += (int) (m_fab_3.getHeight() * 1.7);
        m_fab_3.setLayoutParams(layoutParams3);
        m_fab_3.startAnimation(m_animShowFab3);
        m_fab_3.setClickable(true);*/
    }

    private void hideFab(){

        //Floating Action Button 1
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) m_fab_1.getLayoutParams();
        layoutParams.rightMargin -= (int) (m_fab_1.getWidth() * 1.7);
        layoutParams.bottomMargin -= (int) (m_fab_1.getHeight() * 0.25);
        m_fab_1.setLayoutParams(layoutParams);
        m_fab_1.startAnimation(m_animHideFab1);
        m_fab_1.setClickable(false);

        /*//Floating Action Button 2
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) m_fab_2.getLayoutParams();
        layoutParams2.leftMargin -= (int) (m_fab_2.getWidth() * 1.5);
        layoutParams2.bottomMargin -= (int) (m_fab_2.getHeight() * 1.5);
        m_fab_2.setLayoutParams(layoutParams2);
        m_fab_2.startAnimation(m_animHideFab2);
        m_fab_2.setClickable(false);

        //Floating Action Button 3
        FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) m_fab_3.getLayoutParams();
        layoutParams3.leftMargin -= (int) (m_fab_3.getWidth() * 0.25);
        layoutParams3.bottomMargin -= (int) (m_fab_3.getHeight() * 1.7);
        m_fab_3.setLayoutParams(layoutParams3);
        m_fab_3.startAnimation(m_animHideFab3);
        m_fab_3.setClickable(false);*/
    }
}
