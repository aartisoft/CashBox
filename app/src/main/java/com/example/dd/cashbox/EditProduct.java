package com.example.dd.cashbox;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import SQLite.SQLiteDatabaseHandler_Product;
import adapter.RecyclerViewProductAdapter;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import global.GlobVar;
import objects.ObjCategory;
import objects.ObjProduct;
import recyclerview.RecyclerItemTouchHelper;
import recyclerview.RecyclerItemTouchHelperActions;

public class EditProduct extends AppCompatActivity{

    private RecyclerViewProductAdapter m_adapter;
    private RecyclerItemTouchHelper m_RecyclerItemTouchHelper;
    private RecyclerView m_recyclerview;
    private LinearLayout m_linearlayout;
    private FloatingActionButton m_fab_plus;
    private Context m_Context;
    private View m_decorView;
    private String m_SessionCategory;
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
        setContentView(R.layout.activity_editproduct);

        //activity variables
        m_SessionCategory = getIntent().getStringExtra( "CATEGORY");

        //init variables
        m_Context = this;
        m_linearlayout = findViewById(R.id.editproduct_linearlayout);
        m_recyclerview = findViewById(R.id.editproduct_recycler_view);
        m_fab_plus = findViewById(R.id.editproduct_fab);
        m_decorView = getWindow().getDecorView();

        //set UI
        m_decorView.setSystemUiVisibility(m_uiOptions);
        Toolbar toolbar = findViewById(R.id.toolbar_editproduct);
        //toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTextNoProduct();

        //set RecyclerView
        setupRecyclerView();

        //set Listener
        m_fab_plus.setOnClickListener(fabPlusOnClickListener);
    }

    private View.OnClickListener fabPlusOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(EditProduct.this, EditProduct_Add.class);
            startActivity(intent);
        }
    };


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
                Intent intent = new Intent(EditProduct.this, EditCategory.class);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupRecyclerView(){
        m_adapter = new RecyclerViewProductAdapter(this, GlobVar.m_lstProduct);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        m_recyclerview.setLayoutManager(mLayoutManager);

        m_RecyclerItemTouchHelper = new RecyclerItemTouchHelper(new RecyclerItemTouchHelperActions() {
            @Override
            public void onRightClicked(int position) {
                //get current category
                final ObjProduct product = GlobVar.m_lstProduct.get(position);

                // backup of removed item for undo purpose
                final ObjProduct deletedItem = GlobVar.m_lstProduct.get(position);
                final int deletedIndex = position;

                m_adapter.removeItem(position);
                m_adapter.notifyItemRemoved(position);
                m_adapter.notifyItemRangeChanged(position, m_adapter.getItemCount());

                //delete category in database
                final SQLiteDatabaseHandler_Product db = new SQLiteDatabaseHandler_Product(m_Context);
                db.deleteProduct(product);

                // showing snack bar with Undo option
                Snackbar snackbar = Snackbar
                        .make(m_linearlayout, product.getName() + " " + getResources().getString(R.string.src_Entfernt), Snackbar.LENGTH_LONG);
                snackbar.setAction(getResources().getString(R.string.src_Rueckgaengig), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        // undo is selected, restore the deleted item
                        m_adapter.restoreItem(deletedItem, deletedIndex);
                        db.addProduct(product);
                        setTextNoProduct();
                    }
                });
                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();
                setTextNoProduct();
            }
            @Override
            public void onLeftClicked(int position) {
                ObjCategory obj_category = GlobVar.m_lstCategory.get(position);
                String category = obj_category.getName();

                Intent intent = new Intent(EditProduct.this, EditCategory_Edit.class);
                intent.putExtra("CATEGORY", category);
                startActivity(intent);
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

        m_recyclerview.setAdapter(m_adapter);
        m_adapter.notifyDataSetChanged();
    }

    private void setTextNoProduct(){
        //set text if no category available
        if(!GlobVar.m_lstProduct.isEmpty()){
            findViewById(R.id.editproduct_tv_noproduct).setVisibility(View.INVISIBLE);
        }else{
            findViewById(R.id.editproduct_tv_noproduct).setVisibility(View.VISIBLE);
        }
    }
}
