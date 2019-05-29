package fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.example.dd.cashbox.MainCash;
import com.example.dd.cashbox.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import adapter.ListViewMainCashBillChooseAdapter;
import global.GlobVar;
import objects.ObjBill;
import objects.ObjBillProduct;

public class MainCashBillDialogFragment extends DialogFragment {

    private int m_iTable = -1;
    private int m_iBillNr = -1;
    private String m_strCategory = "";
    private String m_strProduct = "";
    private FloatingActionButton m_fab;
    private ListView m_listView;
    private ListViewMainCashBillChooseAdapter m_adapter;
    private FragmentActivity m_Context;
    private static MainCashBillDialogFragment m_frag;

    public MainCashBillDialogFragment() {
    }

    public static MainCashBillDialogFragment newInstance(String title) {
        m_frag = new MainCashBillDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        m_frag.setArguments(args);
        return m_frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_maincashbill, container, false);

        //activity variables
        m_iTable = getArguments().getInt("TABLE");
        m_iBillNr = getArguments().getInt("BILL");
        m_strCategory = getArguments().getString("CATEGORY");
        m_strProduct = getArguments().getString("PRODUCT");
        m_Context = getActivity();

        //set UI
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        Toolbar toolbar = view.findViewById(R.id.fragment_maincashbill_tb);
        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);
        toolbar.setNavigationOnClickListener(tbOnClickListener);

        //set variables
        m_fab = view.findViewById(R.id.fragment_maincashbill_fab);
        m_listView = view.findViewById(R.id.fragment_maincashbill_lv);

        //set listview
        initListView();

        //set listener
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


    private View.OnClickListener fabOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ArrayList<ObjBillProduct> ObjBillProductList = m_adapter.getObjBillProductList();

            //search if at least one item is checked
            boolean bChecked = false;
            for(ObjBillProduct objBillProduct : ObjBillProductList){
                if(objBillProduct.isChecked()){
                    bChecked = true;
                    break;
                }
            }

            if(bChecked){
                for(ObjBillProduct objBillProduct : GlobVar.g_lstTableBills.get(m_iTable).get(getBillListPointer()).m_lstProducts) {
                    if (objBillProduct.getCategory().equals(m_strCategory)) {
                        if (objBillProduct.getProduct().getName().equals(m_strProduct)) {
                            for (ObjBillProduct objBillProductAdapter : ObjBillProductList) {
                                if(objBillProduct == objBillProductAdapter){
                                    if (objBillProduct.isChecked()) {
                                        objBillProduct.setPayTransit(true);
                                        objBillProduct.setChecked(false);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            else{
                Toast.makeText(m_Context, getResources().getString(R.string.src_KeineProduktAusgewaehlt), Toast.LENGTH_SHORT).show();
            }

            //close fragment
            ((MainCash)m_Context).raiseChange();
            m_frag.dismiss();
        }
    };

    ////////////////////////////////// METHODS ///////////////////////////////////////////////////////////////////

    private void initListView(){
        //init adapter
        ArrayList<ObjBillProduct> lstObjBillProducts = new ArrayList<>();

        for(ObjBillProduct objBillProduct : GlobVar.g_lstTableBills.get(m_iTable).get(getBillListPointer()).m_lstProducts){
            if(objBillProduct.getCategory().equals(m_strCategory)){
                if(objBillProduct.getProduct().getName().equals(m_strProduct)){
                    if( ( !GlobVar.g_bUseMainCash && !objBillProduct.getPayTransit() && !objBillProduct.getPaid() && objBillProduct.getPrinted() && !objBillProduct.getReturned() )
                            || ( GlobVar.g_bUseMainCash && GlobVar.g_bUseSyncBon && !objBillProduct.getPayTransit() && !objBillProduct.getPaid() && objBillProduct.getPrinted() && !objBillProduct.getReturned())
                            || (GlobVar.g_bUseMainCash && !objBillProduct.getPayTransit() && !objBillProduct.getPaid() && !objBillProduct.getReturned())){
                        lstObjBillProducts.add(objBillProduct);
                    }
                }
            }
        }

        m_adapter = new ListViewMainCashBillChooseAdapter(m_Context, lstObjBillProducts);
        m_listView.setAdapter(m_adapter);

    }

    private int getBillListPointer(){
        //get bill
        int iBill = 0;
        for(ObjBill objBill : GlobVar.g_lstTableBills.get(m_iTable)){
            if(objBill.getBillNr() == m_iBillNr){
                return iBill;
            }
            iBill++;
        }
        return 0;
    }
}
