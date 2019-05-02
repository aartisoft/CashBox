package fragments;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.dd.cashbox.Main;
import com.example.dd.cashbox.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import SQLite.SQLiteDatabaseHandler_TableBills;
import adapter.ListViewAllBillAdapter;
import adapter.ListViewRetoureStornoAdapter;
import global.GlobVar;
import objects.ObjBill;
import objects.ObjBillProduct;

public class ViewPagerAllBillFragment extends Fragment{

    Context m_Context;
    private View m_View;
    private ListView m_listView;
    private ListViewAllBillAdapter m_adapter;
    private int m_iSessionTable = -1;
    private int m_iSessionBill = -1;
    private String m_strTask = "";
    private double m_dPrize = 0.00;

    public static Fragment getInstance(int iSessionTable, int iSessionBill, String strTask) {
        Bundle bundle = new Bundle();
        bundle.putInt("TABLE", iSessionTable);
        bundle.putInt("BILL", iSessionBill);
        bundle.putString("TASK", strTask);
        ViewPagerAllBillFragment tabFragment = new ViewPagerAllBillFragment();
        tabFragment.setArguments(bundle);
        return tabFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //activity variables
        m_iSessionTable = getArguments().getInt("TABLE", -1);
        m_iSessionBill = getArguments().getInt("BILL", -1);
        m_strTask = getArguments().getString("TASK");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.childfragment_allbills, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //set variables
        m_Context = getContext();
        m_View = view;
        m_listView = view.findViewById(R.id.fragment_retourestorno_lv);

        //set Listener

        //set listview
        //initListView();
    }

    //////////////////////////////////////////// METHODS /////////////////////////////////////////////////////////////////////
    public void raiseCloseDialog(){
        //setData();
        //update listview bill
        ((Main) getActivity()).raiseNewProduct();

        //close dialog
        Fragment mParentFragment = (RetoureStornoDialogFragment) getParentFragment();
        ((RetoureStornoDialogFragment) mParentFragment).raiseCloseDialog();
    }

    public void raiseClear(){
        m_dPrize = 0.0;
        m_adapter.notifyDataSetChanged();
    }

    /*private void setBills(){
        if(GlobVar.g_lstTableBills.size() != 0){
            m_listViewBillAdapter = new ListViewAllBillAdapter(this, GlobVar.g_lstTableBills.get(0));
            m_ListView.setAdapter(m_listViewBillAdapter);
        }
    }*/

    /*private void initListView(){
        //init adapter
        ArrayList<ObjBillProduct> lstObjBillProducts = new ArrayList<>();

        for(ObjBillProduct objBillProduct : GlobVar.g_lstTableBills.get(m_iSessionTable).get(getBillListPointer()).m_lstProducts){
            if(objBillProduct.getCategory().equals(m_strCategory)){
                if(objBillProduct.getProduct().getName().equals(m_strProduct)){
                    if(m_strTask.equals("returned")) {
                        if(!objBillProduct.getPaid() && objBillProduct.getPrinted() && !objBillProduct.getReturned()){
                            lstObjBillProducts.add(objBillProduct);
                        }
                    }
                    else{
                        if(!objBillProduct.getPaid() && !objBillProduct.getPrinted() && !objBillProduct.getCanceled()){
                            lstObjBillProducts.add(objBillProduct);
                        }
                    }
                }
            }
        }

        if(lstObjBillProducts.size() > 0){
            m_View.findViewById(R.id.fragment_retourestorno_lv_noitem).setVisibility(View.INVISIBLE);
            m_adapter = new ListViewRetoureStornoAdapter(m_Context, lstObjBillProducts);
            m_listView.setAdapter(m_adapter);
        }
        else{
            m_View.findViewById(R.id.fragment_retourestorno_lv_noitem).setVisibility(View.VISIBLE);
        }
    }*/

    /*private void setData(){
        ArrayList<ObjBillProduct> ObjBillProductList = m_adapter.getObjBillProductList();

        for(ObjBillProduct objBillProduct : GlobVar.g_lstTableBills.get(m_iSessionTable).get(getBillListPointer()).m_lstProducts) {
            if (objBillProduct.getCategory().equals(m_strCategory)) {
                if (objBillProduct.getProduct().getName().equals(m_strProduct)) {
                    for (ObjBillProduct objBillProductAdapter : ObjBillProductList) {
                        if(objBillProduct == objBillProductAdapter){
                            if (objBillProduct.isChecked()) {
                                if (m_strTask.equals("returned")) {
                                    objBillProduct.setReturned(true);
                                    objBillProduct.setSqlChanged(true);
                                    objBillProduct.setChecked(false);
                                } else {
                                    objBillProduct.setCanceled(true);
                                    objBillProduct.setSqlChanged(true);
                                    objBillProduct.setChecked(false);
                                }
                            }
                        }
                    }
                }
            }
        }

        //set product in database
        SQLiteDatabaseHandler_TableBills db_tablebills = new SQLiteDatabaseHandler_TableBills(m_Context);
        db_tablebills.addTableBill(m_iSessionTable, m_iSessionBill);
    }*/

    /*private void getPrize(){
        ArrayList<ObjBillProduct> ObjBillProductList = m_adapter.getObjBillProductList();

        for(ObjBillProduct objBillProduct : GlobVar.g_lstTableBills.get(m_iSessionTable).get(getBillListPointer()).m_lstProducts) {
            if (objBillProduct.getCategory().equals(m_strCategory)) {
                if (objBillProduct.getProduct().getName().equals(m_strProduct)) {
                    for (ObjBillProduct objBillProductAdapter : ObjBillProductList) {
                        if(objBillProduct == objBillProductAdapter){
                            if (objBillProduct.isChecked()) {
                                if (m_strTask.equals("returned")) {
                                    m_dPrize += objBillProduct.getVK();
                                    //if pawn is available
                                    if(objBillProduct.getProduct().getbPawn()){
                                        m_dPrize += objBillProduct.getProduct().getPawn();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }*/

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

    public void showPopUpWIndowOk() {
        FragmentManager fm = getChildFragmentManager();
        PopUpWindowOkRetoureStornoFragment popUpWindowOkFragment = PopUpWindowOkRetoureStornoFragment.newInstance();

        // pass table, bill to fragment
        Bundle args = new Bundle();

        //returned
        if(m_strTask.equals("returned")) {

            args.putDouble("CASH", m_dPrize);
        }
        else{
            args.putDouble("CASH", 0.00);
        }

        popUpWindowOkFragment.setArguments(args);
        popUpWindowOkFragment.show(fm, "fragment_popupwindowok");
    }
}
