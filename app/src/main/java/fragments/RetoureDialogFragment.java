package fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.dd.cashbox.EditCategory;
import com.example.dd.cashbox.EditCategory_Edit;
import com.example.dd.cashbox.R;
import com.jaredrummler.android.colorpicker.ColorPickerView;

import org.w3c.dom.Text;

import SQLite.SQLiteDatabaseHandler_TableBills;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import global.GlobVar;
import objects.ObjBill;
import objects.ObjBillProduct;

public class RetoureDialogFragment extends DialogFragment implements View.OnClickListener {

    private Button m_button;
    private Button m_button_min;
    private Button m_button_pl;
    private EditText m_edttCount;
    private TextView m_tvTitle;
    private RetoureDialogListener m_listener;
    private int m_iSessionLVPos = -1;
    private int m_iSessionTable = -1;
    private int m_iSessionBill = -1;
    private int m_iReturned = 0;
    private Context m_Context;
    private static RetoureDialogFragment m_frag;

    public RetoureDialogFragment() {
    }

    public interface RetoureDialogListener {
        void onFinishRetoureDialog();
    }

    public static RetoureDialogFragment newInstance(String title, int color) {
        m_frag = new RetoureDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putInt("color", color);
        m_frag.setArguments(args);
        return m_frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_retoure, container, false);

        //activity variables
        m_iSessionLVPos = getArguments().getInt("POSITION", -1);
        m_iSessionTable = getArguments().getInt("TABLE", -1);
        m_iSessionBill = getArguments().getInt("BILL", 0);

        //set UI
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        Toolbar toolbar = view.findViewById(R.id.fragment_retoure_tb);
        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);
        toolbar.setNavigationOnClickListener(tbOnClickListener);

        //set variables
        m_button = view.findViewById(R.id.fragment_retoure_button);
        m_button_min = view.findViewById(R.id.fragment_retoure_buttonminus);
        m_button_pl = view.findViewById(R.id.fragment_retoure_buttonplus);
        m_edttCount = view.findViewById(R.id.fragment_retoure_edttext);
        m_tvTitle = view.findViewById(R.id.fragment_retoure_tvTitle);
        m_Context = getContext();

        //set Listener
        m_button.setOnClickListener(this);
        m_button_min.setOnClickListener(this);
        m_button_pl.setOnClickListener(this);

        //set edittext
        m_edttCount.setText(String.valueOf(m_iReturned));
        m_edttCount.setCursorVisible(false);

        //set Title
        ObjBillProduct objBillProduct = getObjBillProduct();
        int iQuantity = objBillProduct.getQuantity() - objBillProduct.getCanceled() - objBillProduct.getReturned();
        String strTitle =  iQuantity + "x " + getObjBillProduct().getProduct().getName();
        m_tvTitle.setText(strTitle);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.fragment_retoure_button:
                button_returned();

                //set listener for main
                m_listener = (RetoureDialogListener)getActivity();
                m_listener.onFinishRetoureDialog();

                m_frag.dismiss();
                break;

            case R.id.fragment_retoure_buttonminus:
                button_minus();
                break;

            case R.id.fragment_retoure_buttonplus:
                button_plus();
                break;

            default:

        }
    }

    View.OnClickListener tbOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            m_frag.dismiss();
        }
    };

    private void button_returned(){

        //if value has changed
        if(m_iReturned != 0){
            //get current product and set returned
            final ObjBillProduct objbillproduct = GlobVar.g_lstTableBills.get(m_iSessionTable).get(getBillListPointer()).m_lstProducts.get(m_iSessionLVPos);
            objbillproduct.setReturned(m_iReturned);
            objbillproduct.setSqlChanged(true);

            //set product in database
            SQLiteDatabaseHandler_TableBills db_tablebills = new SQLiteDatabaseHandler_TableBills(m_Context);
            db_tablebills.addTableBill(m_iSessionTable, m_iSessionBill);
        }
    }

    private void button_minus(){
        if(m_iReturned > 0){
            m_iReturned--;
        }

        //set edittext
        m_edttCount.setText(String.valueOf(m_iReturned), TextView.BufferType.EDITABLE);
    }

    private void button_plus(){
        ObjBillProduct objBillProduct = getObjBillProduct();
        int iQuantitiy = objBillProduct.getQuantity() - objBillProduct.getCanceled() - objBillProduct.getReturned();

        if(m_iReturned < iQuantitiy){
            m_iReturned++;
        }

        //set edittext
        m_edttCount.setText(String.valueOf(m_iReturned), TextView.BufferType.EDITABLE);
    }

    private int getBillListPointer(){
        //get bill
        int iBill = 0;
        for(ObjBill objBill : GlobVar.g_lstTableBills.get(m_iSessionTable)){
            if(objBill.getBillNr() == m_iSessionBill){
                return iBill;
            }
            iBill++;
        }
        return 0;
    }

    private ObjBillProduct getObjBillProduct(){
        return GlobVar.g_lstTableBills.get(m_iSessionTable).get(getBillListPointer()).m_lstProducts.get(m_iSessionLVPos);
    }
}
