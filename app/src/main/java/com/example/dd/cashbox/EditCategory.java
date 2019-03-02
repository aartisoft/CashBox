package com.example.dd.cashbox;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
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

import java.util.ArrayList;

import adapter.RecyclerViewCategoryAdapter;
import global.GlobVar;
import objects.ObjCategory;
import recyclerview.RecyclerItemTouchHelper;

public class EditCategory extends AppCompatActivity implements RecyclerViewCategoryAdapter.OnItemClickListener{

    private RecyclerViewCategoryAdapter m_adapter;
    private RecyclerView m_recyclerview;
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
        m_adapter = new RecyclerViewCategoryAdapter(this, GlobVar.m_lstCategory, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        m_recyclerview.setLayoutManager(mLayoutManager);
        m_recyclerview.setItemAnimator(new DefaultItemAnimator());
        m_recyclerview.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        m_recyclerview.setAdapter(m_adapter);
        m_adapter.notifyDataSetChanged();

        //set item touch helper
        //ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        //new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(m_recyclerview);
        RecyclerItemTouchHelper swipeController = new RecyclerItemTouchHelper();
        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
        itemTouchhelper.attachToRecyclerView(m_recyclerview);

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

    private void prepareCategory(){

        //m_lstCategoryList = GlobVar.m_lstCategroy;
    }

    @Override
    public void onItemClick(int position) {

        ObjCategory category = GlobVar.m_lstCategory.get(position);

        Toast.makeText(EditCategory.this, category.getName(), Toast.LENGTH_SHORT).show();
        //Intent intent = new Intent(EditCategory.this, EditCategory_Edit.class);
        //startActivity(intent);
    }
}
