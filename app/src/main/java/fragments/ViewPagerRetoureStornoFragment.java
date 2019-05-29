package fragments;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.example.dd.cashbox.Main;
import com.example.dd.cashbox.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import SQLite.SQLiteDatabaseHandler_TableBills;
import adapter.ListViewRetoureStornoAdapter;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;

import global.GlobVar;
import objects.ObjBill;
import objects.ObjBillProduct;

public class ViewPagerRetoureStornoFragment extends Fragment{

    Context m_Context;
    private View m_View;
    private FloatingActionButton m_fab;
    private ListView m_listView;
    private ListViewRetoureStornoAdapter m_adapter;
    private String m_strCategory = "";
    private String m_strProduct = "";
    private int m_iSessionTable = -1;
    private int m_iSessionBill = -1;
    private String m_strTask = "";
    private double m_dPrize = 0.00;

    public static Fragment getInstance(String strCategory, String strProduct, int iTable, int iBill,  String strTask) {
        Bundle bundle = new Bundle();
        bundle.putString("CATEGORY", strCategory);
        bundle.putString("PRODUCT", strProduct);
        bundle.putInt("TABLE", iTable);
        bundle.putInt("BILL", iBill);
        bundle.putString("TASK", strTask);
        ViewPagerRetoureStornoFragment tabFragment = new ViewPagerRetoureStornoFragment();
        tabFragment.setArguments(bundle);
        return tabFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.childfragment_retourestorno, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //activity variables
        m_strCategory = getArguments().getString("CATEGORY");
        m_strProduct = getArguments().getString("PRODUCT");
        m_iSessionTable = getArguments().getInt("TABLE", -1);
        m_iSessionBill = getArguments().getInt("BILL", -1);
        m_strTask = getArguments().getString("TASK");

        //set variables
        m_Context = getContext();
        m_View = view;
        m_fab = view.findViewById(R.id.fragment_retourestorno_fab);
        m_listView = view.findViewById(R.id.fragment_retourestorno_lv);

        //set Listener
        m_fab.setOnClickListener(fabOnClickListener);

        //set listview
        initListView();
    }

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
                //bill closed?
                if(!GlobVar.g_lstTableBills.get(m_iSessionTable).get(getBillListPointer()).getClosed()){
                    getPrize();

                    //show popupwindow
                    showPopUpWIndowOk();
                }
                else{
                    Toast.makeText(m_Context, getResources().getString(R.string.src_BelegBereitsGeschlossen), Toast.LENGTH_SHORT).show();
                }
            }
            else{
                Toast.makeText(m_Context, getResources().getString(R.string.src_KeineProduktAusgewaehlt), Toast.LENGTH_SHORT).show();
            }

        }
    };

    //////////////////////////////////////////// METHODS /////////////////////////////////////////////////////////////////////
    public void raiseCloseDialog(){
        setData();
        //update listview bill
        ((Main) getActivity()).raiseNewProduct();

        //close dialog
        Fragment mParentFragment = (RetoureStornoDialogFragment) getParentFragment();
        ((RetoureStornoDialogFragment) mParentFragment).raiseCloseDialog();
    }

    public void raiseClear(){
        m_dPrize = 0.0;
        m_adapter.setAllCheckedFalse();
        m_adapter.notifyDataSetChanged();
    }

    private void initListView(){
        //init adapter
        ArrayList<ObjBillProduct> lstObjBillProducts = new ArrayList<>();

        for(ObjBillProduct objBillProduct : GlobVar.g_lstTableBills.get(m_iSessionTable).get(getBillListPointer()).m_lstProducts){
            if(objBillProduct.getCategory().equals(m_strCategory)){
                if(objBillProduct.getProduct().getName().equals(m_strProduct)){
                    //retoure
                    if(m_strTask.equals("returned")) {
                        if( ( !GlobVar.g_bUseMainCash && objBillProduct.getPaid() && objBillProduct.getPrinted() && !objBillProduct.getReturned() && !objBillProduct.getCanceled() )
                                || ( GlobVar.g_bUseMainCash && GlobVar.g_bUseBonPrint && objBillProduct.getPaid() && objBillProduct.getPrinted() && !objBillProduct.getReturned() && !objBillProduct.getCanceled() )
                                || (GlobVar.g_bUseMainCash && objBillProduct.getPaid() && !objBillProduct.getReturned() && !objBillProduct.getCanceled())){
                            lstObjBillProducts.add(objBillProduct);
                        }
                    }
                    //storno
                    else{
                        if(!objBillProduct.getPaid() && !objBillProduct.getCanceled() && !objBillProduct.getReturned() ){
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
    }

    private void setData(){
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
    }

    private void getPrize(){
        ArrayList<ObjBillProduct> ObjBillProductList = m_adapter.getObjBillProductList();

        for(ObjBillProduct objBillProduct : GlobVar.g_lstTableBills.get(m_iSessionTable).get(getBillListPointer()).m_lstProducts) {
            if (objBillProduct.getCategory().equals(m_strCategory)) {
                if (objBillProduct.getProduct().getName().equals(m_strProduct)) {
                    for (ObjBillProduct objBillProductAdapter : ObjBillProductList) {
                        if(objBillProduct == objBillProductAdapter){
                            if (objBillProduct.isChecked()) {
                                if (m_strTask.equals("returned") && objBillProduct.getPaid()) {
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
            args.putDouble("CASH", -1.00);
        }

        popUpWindowOkFragment.setArguments(args);
        popUpWindowOkFragment.show(fm, "fragment_popupwindowok");
    }
}
