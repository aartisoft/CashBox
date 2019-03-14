package com.example.dd.cashbox;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import SQLite.SQLiteDatabaseHandler_Category;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
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


import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.FragmentManager;
import fragments.ChooseColorDialogFragment;
import global.GlobVar;
import objects.ObjCategory;
import objects.ObjPrinter;

public class EditCategory_Add extends AppCompatActivity implements ChooseColorDialogFragment.ChooseColorDialogListener {

    private FloatingActionButton m_fab;
    private Context m_Context;
    private TextView m_TextViewTitle;
    private EditText m_EditTextName;
    private EditText m_EditTextColor;
    private Spinner m_Spinner_Printer;
    private SwitchCompat m_Switch;
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
        setContentView(R.layout.activity_editcategory_editadd);

        //init variables
        m_Context = this;
        m_TextViewTitle = findViewById(R.id.editcategory_add_tititle);
        m_EditTextColor = findViewById(R.id.editcategory_add_tvcolor);
        m_Spinner_Printer = findViewById(R.id.editcategory_add_spinnerprinter);
        m_fab = findViewById(R.id.editcategory_add_fab);
        m_EditTextName = findViewById(R.id.editcategory_add_tvname);
        m_decorView = getWindow().getDecorView();
        m_Switch = findViewById(R.id.editcategory_add_switch);

        //set UI
        m_decorView.setSystemUiVisibility(m_uiOptions);
        Toolbar toolbar = findViewById(R.id.toolbar_editcategory_add);
        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        m_fab.setEnabled(true);
        m_Switch.setChecked(true);
        m_TextViewTitle.setText(R.string.src_KategorieErstellen);

        //set Spinner Printer
        setSpinnerPrinter();


        //set Listener
        m_fab.setOnClickListener(fabOnClickListener);
        m_decorView.getViewTreeObserver().addOnGlobalLayoutListener(softkeyboardOnGlobalLayoutListener);
        m_EditTextName.setOnEditorActionListener(DoneOnEditorActionListener);
        m_EditTextColor.setOnClickListener(OnClickListener);
    }

    private OnClickListener fabOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            //check weather all field are filled
            if(m_EditTextName.getText().toString().equals("") //|| m_ColorPickerView.get
                    || m_Spinner_Printer.getSelectedItem().equals("")){
                Toast.makeText(EditCategory_Add.this, getResources().getString(R.string.src_NichtAlleFelderAusgefuellt), Toast.LENGTH_SHORT).show();
            }
            else {
                //does category already exists?
                boolean b_CategoryExists = false;
                for(ObjCategory category : GlobVar.m_lstCategory){
                    if(category.getName().equals(m_EditTextName.getText().toString())){
                        b_CategoryExists = true;
                        break;
                    }
                }

                if(!b_CategoryExists){
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
                    String macadress = spinnerprinter.substring(spinnerprinter.indexOf(":") +1);
                    for(ObjPrinter printer : GlobVar.m_lstPrinter){
                        if(printer.getMacAddress().equals(macadress)){
                            foundPrinter = printer;
                            break;
                        }
                    }

                    category.setPrinter(foundPrinter);
                    category.setEnabled(m_Switch.isChecked());

                    //save category to global and sql
                    GlobVar.m_lstCategory.add(category);
                    SQLiteDatabaseHandler_Category db = new SQLiteDatabaseHandler_Category(m_Context);
                    db.addCategory(category);

                    Toast.makeText(EditCategory_Add.this, getResources().getString(R.string.src_KategorieAngelegt), Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(EditCategory_Add.this, EditCategory.class);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(EditCategory_Add.this, getResources().getString(R.string.src_KategorieBereitsVorhanden), Toast.LENGTH_SHORT).show();
                }
            }
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
                Intent intent = new Intent(EditCategory_Add.this, EditCategory.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setSpinnerPrinter(){
        m_Spinner_Printer.setPrompt(getResources().getString(R.string.src_DruckerAuswaehlen));

        if(!GlobVar.m_lstPrinter.isEmpty()){
            List<String> categories = new ArrayList<>();
            for(ObjPrinter printer : GlobVar.m_lstPrinter){
                categories.add(printer.getDeviceName() + " - MAC:" + printer.getMacAddress());
            }
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, categories);
            m_Spinner_Printer.setAdapter(dataAdapter);
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
        ChooseColorDialogFragment chooseColorDialogFragment = ChooseColorDialogFragment.newInstance("Some Title", 0);
        chooseColorDialogFragment.show(fm, "fragment_edit_name");

    }

    @Override
    public void onFinishChooseColorDialog(int colorInt) {
        m_EditTextColor.setBackgroundColor(colorInt);
    }
}
