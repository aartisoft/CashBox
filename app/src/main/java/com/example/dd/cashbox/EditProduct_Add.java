package com.example.dd.cashbox;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import SQLite.SQLiteDatabaseHandler_Category;
import SQLite.SQLiteDatabaseHandler_Product;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import fragments.ChooseColorDialogFragment;
import global.GlobVar;
import objects.ObjCategory;
import objects.ObjPrinter;
import objects.ObjProduct;

public class EditProduct_Add extends AppCompatActivity {

    private FloatingActionButton m_fab;
    private Context m_Context;
    private TextView m_TextViewTitle;
    private EditText m_EditTextName;
    private EditText m_EditTextVK;
    private EditText m_EditTextPawn;
    private SwitchCompat m_PawnSwitch;
    private SwitchCompat m_EnableSwitch;
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
        setContentView(R.layout.activity_editproduct_editadd);

        //init variables
        m_Context = this;
        m_TextViewTitle = findViewById(R.id.editproduct_add_tititle);
        m_EditTextName = findViewById(R.id.editproduct_add_tvname);
        m_EditTextVK = findViewById(R.id.editproduct_add_tvvk);
        m_EditTextPawn = findViewById(R.id.editproduct_add_tvpawn);
        m_fab = findViewById(R.id.editproduct_add_fab);
        m_decorView = getWindow().getDecorView();
        m_PawnSwitch = findViewById(R.id.editproduct_add_Pawnswitch);
        m_EnableSwitch = findViewById(R.id.editproduct_add_Enabledswitch);

        //activity variables
        m_SessionCategory = getIntent().getStringExtra( "CATEGORY");

        //set UI
        m_decorView.setSystemUiVisibility(m_uiOptions);
        Toolbar toolbar = findViewById(R.id.toolbar_editproduct_add);
        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        m_fab.setEnabled(true);
        m_PawnSwitch.setChecked(false);
        m_EnableSwitch.setChecked(true);
        m_TextViewTitle.setText(R.string.src_ProduktAnlegen);

        //set Listener
        m_fab.setOnClickListener(fabOnClickListener);
        m_decorView.getViewTreeObserver().addOnGlobalLayoutListener(softkeyboardOnGlobalLayoutListener);
        m_EditTextName.setOnEditorActionListener(DoneOnEditorActionListener);
    }

    private OnClickListener fabOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            //check weather all field are filled
            if (m_EditTextName.getText().toString().equals("") || m_EditTextVK.getText().toString().equals("")
                    || m_EditTextPawn.getText().toString().equals("")) {
                Toast.makeText(EditProduct_Add.this, getResources().getString(R.string.src_NichtAlleFelderAusgefuellt), Toast.LENGTH_SHORT).show();
            } else {
                //does category already exists?
                boolean b_ProductExists = false;
                for (ObjProduct product : GlobVar.m_lstProduct) {
                    if (product.getName().equals(m_EditTextName.getText().toString())) {
                        b_ProductExists = true;
                        break;
                    }
                }

                if (!b_ProductExists) {
                    int indexcounter = 0;
                    for(ObjCategory objcategory : GlobVar.m_lstCategory){
                        if(objcategory.getName().equals(m_SessionCategory)){
                            ObjCategory category = objcategory;
                            List<ObjProduct> lstProduct = category.getListProduct();

                            ObjProduct product = new ObjProduct();
                            product.setName(m_EditTextName.getText().toString());
                            product.setVK(Double.parseDouble(m_EditTextVK.getText().toString()));
                            product.setEnabled(m_EnableSwitch.isChecked());
                            product.setbPAWN(m_PawnSwitch.isChecked());
                            product.setPAWN(Double.parseDouble(m_EditTextPawn.getText().toString()));
                            product.set_Category(m_SessionCategory);
                            lstProduct.add(product);

                            category.setProductList(lstProduct);

                            //save category to global and sql
                            GlobVar.m_lstCategory.set(indexcounter, category);
                            SQLiteDatabaseHandler_Product db = new SQLiteDatabaseHandler_Product(m_Context);
                            db.addProduct(product);

                            Toast.makeText(EditProduct_Add.this, getResources().getString(R.string.src_ProduktWurdeAngelegt), Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(EditProduct_Add.this, EditProduct.class);
                            intent.putExtra("CATEGORY", m_SessionCategory);
                            startActivity(intent);
                            finish();
                        }
                    }
                    indexcounter++;
                } else {
                    Toast.makeText(EditProduct_Add.this, getResources().getString(R.string.src_ProduktBereitsVorhanden), Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    private OnEditorActionListener DoneOnEditorActionListener = new OnEditorActionListener() {

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
            }
            return false;
        }
    };

    private ViewTreeObserver.OnGlobalLayoutListener softkeyboardOnGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            Rect r = new Rect();
            m_decorView.getWindowVisibleDisplayFrame(r);
            int screenHeight = m_decorView.getRootView().getHeight();

            // r.bottom is the position above soft keypad or device button.
            // if keypad is shown, the r.bottom is smaller than that before.
            int keypadHeight = screenHeight - r.bottom;

            //Log.d(TAG, "keypadHeight = " + keypadHeight);

            if (keypadHeight > screenHeight * 0.5) {
                // keyboard is opened
            } else {
                //keyboard is closed
                m_decorView.setSystemUiVisibility(m_uiOptions);
            }
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
        if (hasFocus) {
            m_decorView.setSystemUiVisibility(m_uiOptions);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(EditProduct_Add.this, EditProduct.class);
                intent.putExtra("CATEGORY", m_SessionCategory);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
