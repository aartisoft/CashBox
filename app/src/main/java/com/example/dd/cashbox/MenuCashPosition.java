package com.example.dd.cashbox;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

public class MenuCashPosition extends AppCompatActivity {

    private ListView m_listView;
    private View m_decorView;
    private DrawerLayout m_DrawerLayout;
    private int m_uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menucashpositon);

        //init variables
        m_listView = findViewById(R.id.menucashposition_listview);
        m_decorView = getWindow().getDecorView();
        m_DrawerLayout = findViewById(R.id.drawer_layout);

        //set UI
        m_decorView.setSystemUiVisibility(m_uiOptions);
        Toolbar toolbar = findViewById(R.id.toolbar_menucashposition);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Set an item click listener for ListView
        m_listView.setOnItemClickListener(listviewOnItemClickListener);
    }

    private AdapterView.OnItemClickListener listviewOnItemClickListener = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // Get the selected item text from ListView
            String selectedItem = (String) parent.getItemAtPosition(position);


            if (selectedItem.equals(getResources().getString(R.string.src_AktuellerKassenstand))) {
                startActivity(new Intent(MenuCashPosition.this, CurrCashPosition.class));
            }
            else if(selectedItem.equals(getResources().getString(R.string.src_AlleAbschlussbelege))) {
                //startActivity(new Intent(MenuCashPosition.this, MS_AddPrinter.class));
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
                Intent intent = new Intent(MenuCashPosition.this, Main.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("EXTRA_SESSION_ID", 1);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
