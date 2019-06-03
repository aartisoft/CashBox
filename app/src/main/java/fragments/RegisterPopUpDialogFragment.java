package fragments;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.example.dd.cashbox.Main;
import com.example.dd.cashbox.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import SQLite.SQLiteDatabaseHandler_TableBills;
import global.GlobVar;
import objects.ObjBill;
import objects.ObjBillProduct;
import objects.ObjCategory;
import objects.ObjPrinter;
import objects.ObjProduct;

public class RegisterPopUpDialogFragment extends DialogFragment implements View.OnClickListener {

    private int m_iTable = -1;
    private int m_iBillNr = -1;
    private String m_strCategory = "";
    private String m_strProduct = "";
    private Button m_button_min;
    private Button m_button_pl;
    private EditText m_edttCount;
    private EditText m_edtInfo;
    private EditText m_edtVK;
    private TextView m_tvTitle;
    private SwitchCompat m_ToGoSwitch;
    private SwitchCompat m_ReducedSwitch;
    private FloatingActionButton m_fab;

    private int m_iItems = 0;
    private FragmentActivity m_Context;
    private static RegisterPopUpDialogFragment m_frag;

    public RegisterPopUpDialogFragment() {
    }

    public static RegisterPopUpDialogFragment newInstance(String title) {
        m_frag = new RegisterPopUpDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        m_frag.setArguments(args);
        return m_frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_registerpopup, container, false);

        //activity variables
        m_iTable = getArguments().getInt("TABLE");
        m_iBillNr = getArguments().getInt("BILL");
        m_strCategory = getArguments().getString("CATEGORY");
        m_strProduct = getArguments().getString("PRODUCT");
        m_Context = getActivity();

        //set UI
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        Toolbar toolbar = view.findViewById(R.id.fragment_registerpopup_tb);
        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);
        toolbar.setNavigationOnClickListener(tbOnClickListener);

        //set variables
        m_button_min = view.findViewById(R.id.fragment_registerpopup_page_btnminus);
        m_button_pl = view.findViewById(R.id.fragment_registerpopup_page_btnplus);
        m_tvTitle = view.findViewById(R.id.fragment_registerpopup_tvTitle);
        m_edttCount = view.findViewById(R.id.fragment_registerpopup_page_edttxt);
        m_edtInfo = view.findViewById(R.id.fragment_registerpopup_page_edttxtaddinfo);
        m_edtVK = view.findViewById(R.id.fragment_registerpopup_page_edttxtvk);
        m_fab = view.findViewById(R.id.fragment_registerpopup_page_fab);
        m_ToGoSwitch = view.findViewById(R.id.fragment_registerpopup_page_switch);
        m_ReducedSwitch = view.findViewById(R.id.fragment_registerpopup_page_Reducedswitch);

        //set title
        m_tvTitle.setText(m_strProduct + " - " + m_Context.getString(R.string.src_ExtraInfos));

        //set EditText Counter
        m_edttCount.setText(String.valueOf(0), TextView.BufferType.EDITABLE);
        m_edttCount.setCursorVisible(false);

        //set EditText VK
        m_edtVK.setEnabled(false);

        //set listener
        m_edtVK.setOnTouchListener(vkOnTouchListener);
        m_edtInfo.setOnTouchListener(infoOnTouchListener);
        m_edtVK.setOnEditorActionListener(vkOnEditorActionListener);
        m_edtInfo.setOnEditorActionListener(infoOnEditorActionListener);
        m_button_min.setOnClickListener(this);
        m_button_pl.setOnClickListener(this);
        m_fab.setOnClickListener(fabOnClickListener);
        m_ReducedSwitch.setOnCheckedChangeListener(vkOnCheckedChangeListener);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    View.OnClickListener tbOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            m_frag.dismiss();
        }
    };

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.fragment_registerpopup_page_btnminus:
                button_minus();
                break;

            case R.id.fragment_registerpopup_page_btnplus:
                button_plus();
                break;

            default:

        }
    }

    private View.OnTouchListener infoOnTouchListener = new View.OnTouchListener(){

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            m_edtInfo.setCursorVisible(true);
            return false;
        }
    };

    private View.OnTouchListener vkOnTouchListener = new View.OnTouchListener(){

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            m_edtVK.setCursorVisible(true);
            return false;
        }
    };

    private TextView.OnEditorActionListener infoOnEditorActionListener = new TextView.OnEditorActionListener(){
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                m_edtInfo.setCursorVisible(false);
            }
            return false;
        }
    };

    private TextView.OnEditorActionListener vkOnEditorActionListener = new TextView.OnEditorActionListener(){
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                m_edtVK.setCursorVisible(false);

                //get edittext
                String strPawn = m_edtVK.getText().toString();
                strPawn = strPawn.replace(",", ".");
                double dInput = Double.parseDouble(strPawn);


                //set edittext
                DecimalFormat df = new DecimalFormat("0.00");
                String strOutput = df.format(dInput);
                m_edtVK.setText(strOutput);
            }
            return false;
        }
    };

    private View.OnClickListener fabOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //check weather all field are filled
            if(m_iItems > 0){
                if(m_ReducedSwitch.isChecked() && m_edtVK.getText().toString().equals("")){
                    Toast.makeText(m_Context, getResources().getString(R.string.src_KeinVerkaufspreisAngegeben), Toast.LENGTH_SHORT).show();
                }
                else {
                    writeTableBillsList(m_iTable, m_iBillNr);
                    //tel main activity there is a new product available
                    ((Main) getActivity()).raiseNewProduct();
                    m_frag.dismiss();
                }
            }
            else{
                Toast.makeText(m_Context, getResources().getString(R.string.src_KeineStueckzahlAngegeben), Toast.LENGTH_SHORT).show();
            }
        }
    };

    private CompoundButton.OnCheckedChangeListener vkOnCheckedChangeListener = new CompoundButton.OnCheckedChangeListener(){
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked){
                m_edtVK.setEnabled(true);
            }
            else{
                m_edtVK.setEnabled(false);
            }
        }
    };

    ///////////////////////////////////// METHODS ////////////////////////////////////////////////////////////////////////
    private void button_minus(){
        if(m_iItems > 0){
            m_iItems--;
        }

        //set edittext
        m_edttCount.setText(String.valueOf(m_iItems), TextView.BufferType.EDITABLE);
    }

    private void button_plus(){
        m_iItems++;

        //set edittext
        m_edttCount.setText(String.valueOf(m_iItems), TextView.BufferType.EDITABLE);
    }

    private void setEditTextVK(){
        //get object product
        ObjProduct objproduct = getObjProduct();

        m_edtVK.setText(String.valueOf(objproduct.getVK()), TextView.BufferType.EDITABLE);
    }

    private void writeTableBillsList(int iTable, int iBillNr){

        //get bill
        int iBill = 0;
        for(ObjBill objBill : GlobVar.g_lstTables.get(iTable).g_lstBills){
            if(objBill.getBillNr() == iBillNr){
                break;
            }
            iBill++;
        }

        //get object product
        ObjProduct objproduct = getObjProduct();

        for(int i = 0; i < m_iItems; i++){
            ObjBillProduct objbillproduct = new ObjBillProduct();

            //set id
            String pattern = "ddMMyyyyHHmmss";
            DateFormat df = new SimpleDateFormat(pattern);
            Date date = Calendar.getInstance().getTime();
            String todayAsString = df.format(date);
            GlobVar.g_BillObjID++;
            long lID = Long.parseLong(todayAsString) + GlobVar.g_BillObjID;
            objbillproduct.setID(lID);

            objbillproduct.setProduct(objproduct);

            //set VK
            if(m_ReducedSwitch.isChecked()){
                String strVK = m_edtVK.getText().toString();
                strVK = strVK.replace(",", ".");
                objbillproduct.setVK(Double.parseDouble(strVK));
            }
            else{
                objbillproduct.setVK(objproduct.getVK());
            }

            objbillproduct.setCategory(objproduct.getCategory());
            objbillproduct.setAddInfo(m_edtInfo.getText().toString());
            objbillproduct.setToGo(m_ToGoSwitch.isChecked());

            //get printer
            for(ObjCategory objCategory : GlobVar.g_lstCategory){
                if(objproduct.getCategory().equals(objCategory.getName())){
                    for (ObjPrinter objPrinter : GlobVar.g_lstPrinter) {
                        if(objCategory.getPrinter().getMacAddress().equals(objPrinter.getMacAddress())){
                            objbillproduct.setPrinter(objPrinter);
                        }
                    }
                }
            }

            //add globally
            GlobVar.g_lstTables.get(iTable).g_lstBills.get(iBill).m_lstProducts.add(objbillproduct);

            //write tablebills to database
            //SQLiteDatabaseHandler_TableBills db_tablebills = new SQLiteDatabaseHandler_TableBills(m_Context);
            //db_tablebills.addTableBill(m_iTable, m_iBillNr);
        }
    }

    private ObjProduct getObjProduct(){
        //get object product
        ObjProduct objproduct = new ObjProduct();
        for(ObjCategory objCategory : GlobVar.g_lstCategory){
            if(objCategory.getName().equals(m_strCategory)){
                for(ObjProduct objProduct : objCategory.getListProduct()){
                    if(objProduct.getName().equals(m_strProduct)){
                        objproduct = objProduct;
                    }
                }
            }
        }
        return objproduct;
    }
}
