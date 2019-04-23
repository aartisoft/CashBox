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

public class EditCategory_Edit extends AppCompatActivity implements ChooseColorDialogFragment.ChooseColorDialogListener {

    private FloatingActionButton m_fab;
    private Context m_Context;
    private TextView m_TextViewTitle;
    private EditText m_EditTextName;
    private EditText m_EditTextColor;
    private Spinner m_Spinner_Printer;
    private SwitchCompat m_Switch;
    private View m_decorView;
    private String m_SessionCategory;
    private String m_strCategoryName;
    private int m_iProdColor;
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
        setContentView(R.layout.activity_editcategory_editadd);

        //init variables
        m_Context = this;
        m_TextViewTitle = findViewById(R.id.editcategory_add_tititle);
        m_Spinner_Printer = findViewById(R.id.editcategory_add_spinnerprinter);
        m_fab = findViewById(R.id.editcategory_add_fab);
        m_EditTextName = findViewById(R.id.editcategory_add_tvname);
        m_EditTextColor = findViewById(R.id.editcategory_add_tvcolor);
        m_decorView = getWindow().getDecorView();
        m_Switch = findViewById(R.id.editcategory_add_switch);

        //activity variables
        m_SessionCategory = getIntent().getStringExtra( "CATEGORY");

        //set UI
        m_decorView.setSystemUiVisibility(m_uiOptions);
        Toolbar toolbar = findViewById(R.id.toolbar_editcategory_add);
        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        m_fab.setEnabled(true);
        m_Switch.setChecked(true);
        m_TextViewTitle.setText(R.string.src_KategorieBearbeiten);

        //set values
        setData();

        //set Listener
        m_fab.setOnClickListener(fabOnClickListener);
        m_decorView.getViewTreeObserver().addOnGlobalLayoutListener(softkeyboardOnGlobalLayoutListener);
        m_EditTextName.setOnEditorActionListener(DoneOnEditorActionListener);
        m_EditTextColor.setOnClickListener(OnClickListener);
    }

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

    private OnClickListener OnClickListener = new OnClickListener(){
        @Override
        public void onClick(View v) {
            showColorDialog();
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
    public void onFinishChooseColorDialog(int colorInt) {
        m_EditTextColor.setBackgroundColor(colorInt);

        //set global variables
        m_iProdColor = colorInt;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(EditCategory_Edit.this, EditCategory.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    ///////////////////////////////////////// METHODS //////////////////////////////////////////////////////////////////////
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

    private void setSpinnerPrinter(ObjPrinter p_printer){
        m_Spinner_Printer.setPrompt(getResources().getString(R.string.src_DruckerAuswaehlen));
        int printer_position = 0;

        if(!GlobVar.g_lstPrinter.isEmpty()){
            List<String> categories = new ArrayList<>();

            int counter = 0;
            for(ObjPrinter printer : GlobVar.g_lstPrinter){
                //get position of choosen printer
                if(p_printer != null){
                    if(printer.getMacAddress().equals(p_printer.getMacAddress())){
                        printer_position = counter;
                    }
                }

                categories.add(printer.getDeviceName() + " - MAC:" + printer.getMacAddress());
                counter++;
            }

            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, categories);
            m_Spinner_Printer.setAdapter(dataAdapter);

            m_Spinner_Printer.setSelection(printer_position);
        }
        else{
            List<String> categories = new ArrayList<>();
            categories.add(getResources().getString(R.string.src_KeineDruckerVorhanden));
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
            m_Spinner_Printer.setAdapter(dataAdapter);
        }
    }

    private void showColorDialog() {
        FragmentManager fm = getSupportFragmentManager();
        ChooseColorDialogFragment chooseColorDialogFragment = ChooseColorDialogFragment.newInstance("Some Title", m_iProdColor);
        chooseColorDialogFragment.show(fm, "fragment_edit_name");

    }

    private void setData(){
        for(ObjCategory category : GlobVar.g_lstCategory){
            if(category.getName().equals(m_SessionCategory)){
                m_EditTextName.setText(category.getName());
                m_strCategoryName = category.getName();
                m_EditTextColor.setBackgroundColor(category.getProdColor());
                setSpinnerPrinter(category.getPrinter());
                m_Switch.setChecked(category.getEnabled());

                //set global variables
                m_iProdColor = category.getProdColor();
            }
        }
    }

    private void getData(){
        //check weather all field are filled
        if(m_EditTextName.getText().toString().equals("") //|| m_ColorPickerView.get
                || m_Spinner_Printer.getSelectedItem().equals("")){
            Toast.makeText(EditCategory_Edit.this, getResources().getString(R.string.src_NichtAlleFelderAusgefuellt), Toast.LENGTH_SHORT).show();
        }
        else {
            //does category already exists?
            boolean b_CategoryExists = false;
            for (ObjCategory objcategory : GlobVar.g_lstCategory) {
                if (objcategory.getName().equals(m_EditTextName.getText().toString())) {
                    if (!objcategory.getName().equals(m_SessionCategory)) {
                        b_CategoryExists = true;
                        break;
                    }
                }
            }
            int indexcounter = 0;
            for (ObjCategory objcategory : GlobVar.g_lstCategory) {
                if (objcategory.getName().equals(m_SessionCategory)) {
                    if (!b_CategoryExists) {
                        ObjCategory category = new ObjCategory();
                        category.setName(m_EditTextName.getText().toString());

                        //ColorDrawable can throw exception
                        try{
                            ColorDrawable viewColor = (ColorDrawable) m_EditTextColor.getBackground();
                            category.setProdColor(viewColor.getColor());
                        }
                        catch(Exception e){
                            category.setProdColor(1);
                        }

                        //get object printer
                        ObjPrinter foundPrinter = new ObjPrinter();
                        foundPrinter = null;
                        String spinnerprinter = m_Spinner_Printer.getSelectedItem().toString();
                        String macadress = spinnerprinter.substring(spinnerprinter.indexOf(":") + 1);
                        for (ObjPrinter printer : GlobVar.g_lstPrinter) {
                            if (printer.getMacAddress().equals(macadress)) {
                                foundPrinter = printer;
                                break;
                            }
                        }

                        category.setPrinter(foundPrinter);
                        category.setEnabled(m_Switch.isChecked());
                        category.setProductList(objcategory.getListProduct());

                        //update global and in database
                        GlobVar.g_lstCategory.set(indexcounter, category);
                        SQLiteDatabaseHandler_Category db = new SQLiteDatabaseHandler_Category(m_Context);
                        db.updateCategory(m_strCategoryName, category);

                        //if categoryname has changed --> update global and in database
                        if(!m_SessionCategory.equals(m_EditTextName.getText().toString())){
                            for(ObjProduct objproduct : objcategory.getListProduct()){
                                objproduct.set_Category(m_EditTextName.getText().toString());
                            }

                            SQLiteDatabaseHandler_Product db_product = new SQLiteDatabaseHandler_Product(m_Context);
                            db_product.updateProductsCategory(m_SessionCategory, m_EditTextName.getText().toString());
                        }


                        Toast.makeText(EditCategory_Edit.this, getResources().getString(R.string.src_KategorieGeaendert), Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(EditCategory_Edit.this, EditCategory.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(EditCategory_Edit.this, getResources().getString(R.string.src_KategorieNameBereitsVorhanden), Toast.LENGTH_SHORT).show();
                    }
                }
                indexcounter++;
            }
        }
    }
}
