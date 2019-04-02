package fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.dd.cashbox.R;
import com.jaredrummler.android.colorpicker.ColorPickerView;

import SQLite.SQLiteDatabaseHandler_TableBills;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import global.GlobVar;
import objects.ObjBill;
import objects.ObjBillProduct;

public class RetoureDialogFragment extends DialogFragment implements View.OnClickListener {

    private Button m_button;
    private Button m_button_min;
    private Button m_button_pl;
    private int m_iSessionLVPos = -1;
    private int m_iSessionTable = -1;
    private int m_iSessionBill = -1;
    private int m_iReturned = 0;
    private Context m_Context;
    private static RetoureDialogFragment m_frag;

    public RetoureDialogFragment() {
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

        //set variables
        m_button = view.findViewById(R.id.fragment_retoure_button);
        m_button_min = view.findViewById(R.id.fragment_retoure_buttonminus);
        m_button_pl = view.findViewById(R.id.fragment_retoure_buttonplus);
        m_Context = getContext();

        //set Listener
        m_button.setOnClickListener(this);
        m_button_min.setOnClickListener(this);
        m_button_pl.setOnClickListener(this);

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
                m_frag.dismiss();

            case R.id.fragment_retoure_buttonminus:
                button_minus();

            case R.id.fragment_retoure_buttonplus:
                button_plus();

            default:

        }
    }

    private void button_returned(){
        //get current product and set returned
        final ObjBillProduct objbillproduct = GlobVar.g_lstTableBills.get(m_iSessionTable).get(getBillListPointer()).m_lstProducts.get(m_iSessionLVPos);
        objbillproduct.setReturned(m_iReturned);
        objbillproduct.setSqlChanged(true);

        //set product in database
        SQLiteDatabaseHandler_TableBills db_tablebills = new SQLiteDatabaseHandler_TableBills(m_Context);
        db_tablebills.addTableBill(m_iSessionTable, getBillListPointer());
    }

    private void button_minus(){
        if(m_iReturned > 0){
            m_iReturned--;
        }
    }

    private void button_plus(){
        m_iReturned++;
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
}
