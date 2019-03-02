package com.example.dd.cashbox;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import SQLite.SQLiteDatabaseHandler_Category;
import adapter.ListViewPrinterAdapter;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;

import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Toast;


import adapter.RecyclerViewCategoryAdapter;
import global.GlobVar;
import objects.ObjCategory;
import recyclerview.RecyclerItemTouchHelper;
import recyclerview.RecyclerItemTouchHelperActions;

public class EditCategory extends AppCompatActivity implements RecyclerViewCategoryAdapter.OnItemClickListener{

    private RecyclerViewCategoryAdapter m_adapter;
    private RecyclerItemTouchHelper m_RecyclerItemTouchHelper;
    private RecyclerView m_recyclerview;
    private LinearLayout m_linearlayout;
    private FloatingActionButton m_fab_plus;
    private Context m_Context;
    private View m_decorView;
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
        setContentView(R.layout.activity_editcategory);

        //init variables
        m_Context = this;
        m_linearlayout = findViewById(R.id.editcategory_linearlayout);
        m_recyclerview = findViewById(R.id.editcategory_recycler_view);
        m_fab_plus = findViewById(R.id.editcategory_fab);
        m_decorView = getWindow().getDecorView();

        //set UI
        m_decorView.setSystemUiVisibility(m_uiOptions);
        Toolbar toolbar = findViewById(R.id.toolbar_editcategory);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //set RecyclerView
        setupRecyclerView();

        //prepare Category

        //set Listener
        m_fab_plus.setOnClickListener(fabPlusOnClickListener);
    }

    private View.OnClickListener fabPlusOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(EditCategory.this, EditCategory_Add.class);
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
                Intent intent = new Intent(EditCategory.this, Main.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("EXTRA_SESSION_ID", 1);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*@Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof RecyclerViewCategoryAdapter.MyViewHolder) {
            // get the removed item name to display it in snack bar
            String name = GlobVar.m_lstCategory.get(viewHolder.getAdapterPosition()).getName();

            // backup of removed item for undo purpose
            final ObjCategory deletedItem = GlobVar.m_lstCategory.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            // remove the item from recycler view
            m_adapter.removeItem(viewHolder.getAdapterPosition());

            // showing snack bar with Undo option
            Snackbar snackbar = Snackbar
                    .make(m_linearlayour, name + " " + getResources().getString(R.string.src_Entfernt), Snackbar.LENGTH_LONG);
            snackbar.setAction(getResources().getString(R.string.src_Rueckgaengig), new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // undo is selected, restore the deleted item
                    m_adapter.restoreItem(deletedItem, deletedIndex);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }*/

    @Override
    public void onItemClick(int position) {

        ObjCategory category = GlobVar.m_lstCategory.get(position);

        Toast.makeText(EditCategory.this, category.getName(), Toast.LENGTH_SHORT).show();
        //Intent intent = new Intent(EditCategory.this, EditCategory_Edit.class);
        //startActivity(intent);
    }


    private void setupRecyclerView(){
        m_adapter = new RecyclerViewCategoryAdapter(this, GlobVar.m_lstCategory, this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        m_recyclerview.setLayoutManager(mLayoutManager);

        m_RecyclerItemTouchHelper = new RecyclerItemTouchHelper(new RecyclerItemTouchHelperActions() {
            @Override
            public void onRightClicked(int position) {
                //get current category
                final ObjCategory category = GlobVar.m_lstCategory.get(position);

                // backup of removed item for undo purpose
                final ObjCategory deletedItem = GlobVar.m_lstCategory.get(position);
                final int deletedIndex = position;

                m_adapter.removeItem(position);
                m_adapter.notifyItemRemoved(position);
                m_adapter.notifyItemRangeChanged(position, m_adapter.getItemCount());

                //delete category in database
                final SQLiteDatabaseHandler_Category db = new SQLiteDatabaseHandler_Category(m_Context);
                db.deleteCategory(category);

                // showing snack bar with Undo option
                Snackbar snackbar = Snackbar
                        .make(m_linearlayout, category.getName() + " " + getResources().getString(R.string.src_Entfernt), Snackbar.LENGTH_LONG);
                snackbar.setAction(getResources().getString(R.string.src_Rueckgaengig), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        // undo is selected, restore the deleted item
                        m_adapter.restoreItem(deletedItem, deletedIndex);
                        db.addCategory(category);
                    }
                });
                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();
            }
            @Override
            public void onLeftClicked(int position) {
                ObjCategory obj_category = GlobVar.m_lstCategory.get(position);
                String category = obj_category.getName();

                Intent intent = new Intent(EditCategory.this, EditCategory_Edit.class);
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
}
