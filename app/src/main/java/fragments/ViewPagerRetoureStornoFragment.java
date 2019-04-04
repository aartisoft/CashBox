package fragments;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dd.cashbox.Main;
import com.example.dd.cashbox.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

import SQLite.SQLiteDatabaseHandler_TableBills;
import adapter.GridViewProductAdapter;
import adapter.ViewPagerRetoureStornoAdapter;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;
import global.GlobVar;
import objects.ObjBill;
import objects.ObjBillProduct;
import objects.ObjCategory;
import objects.ObjPrinter;
import objects.ObjProduct;

public class ViewPagerRetoureStornoFragment extends Fragment implements View.OnClickListener{

    int m_position;
    private Button m_button;
    private Button m_button_min;
    private Button m_button_pl;
    private EditText m_edttCount;
    private TextView m_tvTitle;
    private RetoureStornoDialogFragment.RetoureStornoDialogListener m_listener;
    private ViewPagerRetoureStornoAdapter m_ViewPagerAdapter;
    private TabLayout m_TabLayout;
    private ViewPager m_ViewPager;
    Context m_Context;
    private int m_iSessionLVPos = -1;
    private int m_iSessionTable = -1;
    private int m_iSessionBill = -1;
    private int m_iReturned = 0;
    private GridView m_GridView;
    private GridViewProductAdapter m_gridViewProductAdapter;

    public static Fragment getInstance(int position) {
        Bundle bundle = new Bundle();
        bundle.putInt("pos", position);
        ViewPagerRetoureStornoFragment tabFragment = new ViewPagerRetoureStornoFragment();
        tabFragment.setArguments(bundle);
        return tabFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_position = getArguments().getInt("pos");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tabregister, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //activity variables
        m_iSessionLVPos = getArguments().getInt("POSITION", -1);
        m_iSessionTable = getArguments().getInt("TABLE", -1);
        m_iSessionBill = getArguments().getInt("BILL", 0);


        //set variables
        m_button = getActivity().findViewById(R.id.fragment_retoure_button);
        m_button_min = getActivity().findViewById(R.id.fragment_retoure_buttonminus);
        m_button_pl = getActivity().findViewById(R.id.fragment_retoure_buttonplus);
        m_edttCount = getActivity().findViewById(R.id.fragment_retoure_edttext);
        m_tvTitle = getActivity().findViewById(R.id.fragment_retoure_tvTitle);
        m_Context = getContext();

        //set Listener
        m_button.setOnClickListener(this);
        m_button_min.setOnClickListener(this);
        m_button_pl.setOnClickListener(this);

        //set edittext
        m_edttCount.setText(String.valueOf(m_iReturned));
        m_edttCount.setCursorVisible(false);

        //set Listener;

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.fragment_retoure_button:
                button_returned();

                //set listener for main
                m_listener = (RetoureStornoDialogFragment.RetoureStornoDialogListener)getActivity();
                m_listener.onFinishRetoureStornoDialog();

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

    private void button_returned(){

        //if value has changed
        if(m_iReturned != 0) {
            //get current product and set returned
            final ObjBillProduct objbillproduct = GlobVar.g_lstTableBills.get(m_iSessionTable).get(getBillListPointer()).m_lstProducts.get(m_iSessionLVPos);

            int iReturned = objbillproduct.getReturned() + m_iReturned;
            objbillproduct.setReturned(iReturned);
            objbillproduct.setSqlChanged(true);

            //set product in database
            SQLiteDatabaseHandler_TableBills db_tablebills = new SQLiteDatabaseHandler_TableBills(m_Context);
            db_tablebills.addTableBill(m_iSessionTable, m_iSessionBill);
            showPopUpWIndowOk();
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

    public void showPopUpWIndowOk() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        PopUpWindowOkFragment popUpWindowOkFragment = PopUpWindowOkFragment.newInstance();

        // pass table, bill to fragment
        Bundle args = new Bundle();
        //args.putInt("POSITION", position);

        popUpWindowOkFragment.setArguments(args);
        popUpWindowOkFragment.show(fm, "fragment_popupwindowok");
    }
}
