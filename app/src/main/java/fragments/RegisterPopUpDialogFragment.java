package fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.dd.cashbox.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import adapter.ViewPagerRetoureStornoAdapter;
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
    private SwitchCompat m_Switch;
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

        //set UI
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        Toolbar toolbar = view.findViewById(R.id.fragment_registerpopup_tb);
        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);
        toolbar.setNavigationOnClickListener(tbOnClickListener);

        //set variables
        m_Context = getActivity();
        m_button_min = view.findViewById(R.id.fragment_registerpopup_page_btnminus);
        m_button_pl = view.findViewById(R.id.fragment_registerpopup_page_btnplus);
        m_edttCount = view.findViewById(R.id.fragment_registerpopup_page_edttxt);
        m_edtInfo= view.findViewById(R.id.fragment_registerpopup_page_ticom);
        m_edtVK = view.findViewById(R.id.fragment_registerpopup_page_tivk);
        m_fab = view.findViewById(R.id.fragment_registerpopup_page_fab);
        m_Switch = view.findViewById(R.id.fragment_registerpopup_page_switch);

        //set EditText
        m_edttCount.setText(String.valueOf(0), TextView.BufferType.EDITABLE);
        m_edttCount.setCursorVisible(false);

        //set listener
        m_button_min.setOnClickListener(this);
        m_button_pl.setOnClickListener(this);
        m_fab.setOnClickListener(fabOnClickListener);

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

    private View.OnClickListener fabOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //check weather all field are filled
            if(m_iItems > 0){
                if(m_edtVK.getText().toString().equals("")){
                    Toast.makeText(m_Context, getResources().getString(R.string.src_KeinVerkaufspreisAngegeben), Toast.LENGTH_SHORT).show();
                }
                else {
                    writeTableBillsList(m_iTable, m_iBillNr);
                }
            }
        }
    };

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

    private void writeTableBillsList(int iTable, int iBillNr){

        //get bill
        int iBill = 0;
        for(ObjBill objBill : GlobVar.g_lstTableBills.get(iTable)){
            if(objBill.getBillNr() == iBillNr){
                break;
            }
            iBill++;
        }

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

        for(int i = 0; i <= m_iItems; i++){
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
            objbillproduct.setVK(Double.parseDouble(m_edtVK.getText().toString()));
            objbillproduct.setCategory(objproduct.getCategory());
            objbillproduct.setAddInfo(m_edtInfo.getText().toString());
            objbillproduct.setToGo(m_Switch.isChecked());

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
            GlobVar.g_lstTableBills.get(iTable).get(iBill).m_lstProducts.add(objbillproduct);
        }
    }
}
