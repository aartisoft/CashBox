package com.example.dd.cashbox;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import SQLite.SQLiteDatabaseHandler_Product;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import global.GlobVar;
import objects.ObjCategory;
import objects.ObjProduct;

public class EditProduct_Edit extends AppCompatActivity {

    private FloatingActionButton m_fab;
    private Context m_Context;
    private TextView m_TextViewTitle;
    private EditText m_EditTextName;
    private EditText m_EditTextVK;
    private EditText m_EditTextPawn;
    private SwitchCompat m_PawnSwitch;
    private SwitchCompat m_EnableSwitch;
    private Spinner m_Spinner_Tax;
    private View m_decorView;
    private String m_SessionCategory;
    private String m_SessionProduct;
    private List<ObjProduct> m_lstProduct = new ArrayList<ObjProduct>();
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
        m_TextViewTitle = findViewById(R.id.editcategory_add_tititle);
        m_TextViewTitle = findViewById(R.id.editproduct_add_tititle);
        m_EditTextName = findViewById(R.id.editproduct_add_tvname);
        m_EditTextVK = findViewById(R.id.editproduct_add_tvvk);
        m_EditTextPawn = findViewById(R.id.editproduct_add_tvpawn);
        m_fab = findViewById(R.id.editproduct_add_fab);
        m_decorView = getWindow().getDecorView();
        m_PawnSwitch = findViewById(R.id.editproduct_add_Pawnswitch);
        m_EnableSwitch = findViewById(R.id.editproduct_add_Enabledswitch);
        m_Spinner_Tax = findViewById(R.id.editproduct_add_spinnertax);

        //activity variables
        m_SessionCategory = getIntent().getStringExtra( "CATEGORY");
        m_SessionProduct = getIntent().getStringExtra( "PRODUCT");

        //set UI
        m_decorView.setSystemUiVisibility(m_uiOptions);
        Toolbar toolbar = findViewById(R.id.toolbar_editproduct_add);
        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        m_fab.setEnabled(true);
        m_PawnSwitch.setChecked(false);
        m_EditTextPawn.setEnabled(false);
        m_EnableSwitch.setChecked(true);
        m_TextViewTitle.setText(R.string.src_ProduktBearbeiten);

        //get product list
        getCurrentProductList();

        //set values
        setData();

        //set Listener
        m_fab.setOnClickListener(fabOnClickListener);
        m_decorView.getViewTreeObserver().addOnGlobalLayoutListener(softkeyboardOnGlobalLayoutListener);
        m_EditTextName.setOnEditorActionListener(DoneOnEditorActionListener);
        m_PawnSwitch.setOnCheckedChangeListener(pawnOnCheckedChangeListener);
    }

    private CompoundButton.OnCheckedChangeListener pawnOnCheckedChangeListener = new CompoundButton.OnCheckedChangeListener(){
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked){
                m_EditTextPawn.setEnabled(true);
            }
            else{
                m_EditTextPawn.setEnabled(false);
            }
        }
    };

    private OnClickListener fabOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            getData();
        }
    };

    private OnEditorActionListener DoneOnEditorActionListener = new OnEditorActionListener(){
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
            }
            return false;
        }
    };

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

            if (keypadHeight > screenHeight * 0.5) {
                // keyboard is opened
            }
            else {
                //keyboard is closed
                m_EditTextName.setCursorVisible(false);
                m_EditTextVK.setCursorVisible(false);
                m_EditTextPawn.setCursorVisible(false);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(EditProduct_Edit.this, EditProduct.class);
                intent.putExtra("CATEGORY", m_SessionCategory);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //////////////////////////////////// METHODS /////////////////////////////////////////////////////////////////

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

    private void getCurrentProductList(){
        for(ObjCategory objcategory : GlobVar.g_lstCategory) {
            if (objcategory.getName().equals(m_SessionCategory)) {
                m_lstProduct = objcategory.getListProduct();
                break;
            }
        }
    }

    private void setData(){
        for(ObjProduct product : m_lstProduct){
            if(product.getName().equals(m_SessionProduct)){
                DecimalFormat df = new DecimalFormat("#.00");

                m_EditTextName.setText(product.getName());

                String vk = String.valueOf(df.format(product.getVK()));
                m_EditTextVK.setText(vk);

                //set pawn
                m_PawnSwitch.setChecked(product.getbPawn());
                String strPawn = "";
                if(product.getbPawn()){
                    strPawn = String.valueOf(df.format(product.getPawn()));
                    m_EditTextPawn.setEnabled(true);
                }
                else{
                    strPawn = "";
                    m_EditTextPawn.setEnabled(false);
                }
                m_EditTextPawn.setText(strPawn);

                m_EnableSwitch.setChecked(product.getEnabled());
            }
        }

        //set taxes spinner
        setSpinnerTax();
    }

    private void getData(){
        //check weather all field are filled
        if (m_EditTextName.getText().toString().equals("") || m_EditTextVK.getText().toString().equals("")
                || ((m_EditTextPawn.getText().toString().equals("")) && m_PawnSwitch.isChecked()) || m_Spinner_Tax.getSelectedItem().equals(""))   {
            Toast.makeText(EditProduct_Edit.this, getResources().getString(R.string.src_NichtAlleFelderAusgefuellt), Toast.LENGTH_SHORT).show();
        }
        else {
            //does category already exists?
            boolean b_ProductExists = false;
            for (ObjProduct product : m_lstProduct) {
                if (product.getName().equals(m_EditTextName.getText().toString())) {
                    if (!product.getName().equals(m_SessionProduct)) {
                        b_ProductExists = true;
                        break;
                    }
                }
            }

            if (!b_ProductExists) {
                int indexcounter = 0;
                for(ObjCategory objcategory : GlobVar.g_lstCategory){
                    if(objcategory.getName().equals(m_SessionCategory)){
                        ObjCategory category = objcategory;
                        List<ObjProduct> lstProduct = category.getListProduct();

                        int indexcounter_prod = 0;
                        for(ObjProduct objproduct : lstProduct){
                            if(objproduct.getName().equals(m_SessionProduct)){
                                ObjProduct product = new ObjProduct();
                                product.setName(m_EditTextName.getText().toString());

                                //set VK
                                String strVK = m_EditTextVK.getText().toString();
                                strVK = strVK.replace(",", ".");
                                product.setVK(Double.parseDouble(strVK));
                                product.setEnabled(m_EnableSwitch.isChecked());

                                //set pawn
                                product.setbPAWN(m_PawnSwitch.isChecked());
                                if(m_PawnSwitch.isChecked()){
                                    String strPawn = m_EditTextPawn.getText().toString();
                                    strPawn = strPawn.replace(",", ".");
                                    product.setPAWN(Double.parseDouble(strPawn));
                                }
                                else{
                                    product.setPAWN(0.00);
                                }

                                //set tax
                                String strTax = m_Spinner_Tax.getSelectedItem().toString();
                                strTax.replace("%", "");
                                product.setTax(Double.parseDouble(strTax));

                                product.set_Category(m_SessionCategory);
                                lstProduct.set(indexcounter_prod, product);

                                category.setProductList(lstProduct);

                                //save category to global and sql
                                GlobVar.g_lstCategory.set(indexcounter, category);
                                SQLiteDatabaseHandler_Product db = new SQLiteDatabaseHandler_Product(m_Context);
                                db.updateProduct(m_SessionProduct, product);

                                Toast.makeText(EditProduct_Edit.this, getResources().getString(R.string.src_ProduktGaendert), Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(EditProduct_Edit.this, EditProduct.class);
                                intent.putExtra("CATEGORY", m_SessionCategory);
                                startActivity(intent);
                                finish();
                                break;
                            }
                            indexcounter_prod++;
                        }
                    }
                    indexcounter++;
                }
            }
            else {
                Toast.makeText(EditProduct_Edit.this, getResources().getString(R.string.src_ProduktNameBereitsVorhanden), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setSpinnerTax(){
        m_Spinner_Tax.setPrompt(getResources().getString(R.string.src_MehrwertsteuerAuswaehlen));
        int printer_position = 0;

        List<String> taxes = new ArrayList<>();
        taxes.add("7%");
        taxes.add("19%");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, taxes);
        m_Spinner_Tax.setAdapter(dataAdapter);

        m_Spinner_Tax.setSelection(printer_position);

    }
}
