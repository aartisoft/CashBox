package fragments;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.example.dd.cashbox.Main;
import com.example.dd.cashbox.R;
import com.google.android.material.tabs.TabLayout;

import SQLite.SQLiteDatabaseHandler_TableBills;
import adapter.GridViewProductAdapter;
import adapter.ViewPagerRetoureStornoAdapter;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;
import global.GlobVar;
import objects.ObjBill;
import objects.ObjBillProduct;

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
    private String m_strTask = "";
    private int m_iItems = 0;
    private GridView m_GridView;
    private GridViewProductAdapter m_gridViewProductAdapter;

    public static Fragment getInstance(int position, int iSessionTable, int iSessionBill, String strTask) {
        Bundle bundle = new Bundle();
        bundle.putInt("POSITION", position);
        bundle.putInt("TABLE", iSessionTable);
        bundle.putInt("BILL", iSessionBill);
        bundle.putString("TASK", strTask);
        ViewPagerRetoureStornoFragment tabFragment = new ViewPagerRetoureStornoFragment();
        tabFragment.setArguments(bundle);
        return tabFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //activity variables
        m_iSessionLVPos = getArguments().getInt("POSITION", -1);
        m_iSessionTable = getArguments().getInt("TABLE", -1);
        m_iSessionBill = getArguments().getInt("BILL", 0);
        m_strTask = getArguments().getString("TASK");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.childfragment_retourestorno, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //set variables
        m_button = view.findViewById(R.id.fragment_retoure_button);
        m_button_min = view.findViewById(R.id.fragment_retoure_buttonminus);
        m_button_pl = view.findViewById(R.id.fragment_retoure_buttonplus);
        m_edttCount = view.findViewById(R.id.fragment_retoure_edttext);
        m_Context = getContext();

        //set Listener
        m_button.setOnClickListener(this);
        m_button_min.setOnClickListener(this);
        m_button_pl.setOnClickListener(this);

        //set edittext
        m_edttCount.setText(String.valueOf(m_iItems));
        m_edttCount.setCursorVisible(false);

        //set button
        setButton();

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.fragment_retoure_button:
                button_returned();
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

    public void raiseCloseDialog(){
        //update listview bill
        ((Main) getActivity()).raiseNewProduct();

        //close dialog
        Fragment mParentFragment = (RetoureStornoDialogFragment) getParentFragment();
        ((RetoureStornoDialogFragment) mParentFragment).raiseCloseDialog();
    }

    private void button_returned(){

        //if value has changed
        if(m_iItems != 0) {
            //get current product and set returned
            final ObjBillProduct objbillproduct = GlobVar.g_lstTableBills.get(m_iSessionTable).get(getBillListPointer()).m_lstProducts.get(m_iSessionLVPos);

            //returned
            if(m_strTask.equals("returned")){
                int iReturned = objbillproduct.getReturned() + m_iItems;
                objbillproduct.setReturned(iReturned);

                //set printed minus one
                int iPrinted = objbillproduct.getPrinted() -m_iItems;
                objbillproduct.setPrinted(iPrinted);

                objbillproduct.setSqlChanged(true);
            }
            //canceled
            else{
                int iCanceled = objbillproduct.getCanceled() + m_iItems;
                objbillproduct.setCanceled(iCanceled);
                objbillproduct.setSqlChanged(true);
            }

            //set product in database
            SQLiteDatabaseHandler_TableBills db_tablebills = new SQLiteDatabaseHandler_TableBills(m_Context);
            db_tablebills.addTableBill(m_iSessionTable, m_iSessionBill);
            showPopUpWIndowOk();
        }

    }

    private void button_minus(){
        if(m_iItems > 0){
            m_iItems--;
        }

        //set edittext
        m_edttCount.setText(String.valueOf(m_iItems), TextView.BufferType.EDITABLE);
    }

    private void button_plus(){
        //get task quantitiy
        int iQuantitiy = getQuantitiy();

        if(m_iItems < iQuantitiy){
            m_iItems++;
        }

        //set edittext
        m_edttCount.setText(String.valueOf(m_iItems), TextView.BufferType.EDITABLE);
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

    private void setButton(){
        //returned
        if(m_strTask.equals("returned")){
            m_button.setText(getResources().getString(R.string.src_Retoure));
        }
        //canceled
        else{
            m_button.setText(getResources().getString(R.string.src_Storno));
        }
    }

    private int getQuantitiy(){
        ObjBillProduct objBillProduct = getObjBillProduct();

        //returned
        if(m_strTask.equals("returned")){
            int iQuantitiy = objBillProduct.getPrinted();
            return iQuantitiy;
        }
        //canceled
        else{
            int iQuantitiy = objBillProduct.getQuantity() - objBillProduct.getPrinted() - objBillProduct.getReturned() -objBillProduct.getCanceled();
            return iQuantitiy;
        }
    }

    public void showPopUpWIndowOk() {
        FragmentManager fm = getChildFragmentManager();
        PopUpWindowOkRetoureStornoFragment popUpWindowOkFragment = PopUpWindowOkRetoureStornoFragment.newInstance();

        // pass table, bill to fragment
        Bundle args = new Bundle();

        //calculate return prize
        //returned
        if(m_strTask.equals("returned")) {
            double prize = getObjBillProduct().getProduct().getVK() * m_iItems;
            args.putDouble("CASH", prize);
        }
        else{
            args.putDouble("CASH", 0.00);
        }

        popUpWindowOkFragment.setArguments(args);
        popUpWindowOkFragment.show(fm, "fragment_popupwindowok");
    }
}
