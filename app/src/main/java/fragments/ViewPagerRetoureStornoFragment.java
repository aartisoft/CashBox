package fragments;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.dd.cashbox.Main;
import com.example.dd.cashbox.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import SQLite.SQLiteDatabaseHandler_TableBills;
import adapter.GridViewProductAdapter;
import adapter.ListViewPrinterSearchAdapter;
import adapter.ListViewRetoureStornoAdapter;
import adapter.ViewPagerRetoureStornoAdapter;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import java.text.DecimalFormat;
import java.util.ArrayList;

import global.GlobVar;
import objects.ObjBill;
import objects.ObjBillProduct;
import objects.ObjCategory;
import objects.ObjPrinterSearch;
import objects.ObjProduct;

public class ViewPagerRetoureStornoFragment extends Fragment{

    int m_position;
    private FloatingActionButton m_fab;
    private TextView m_tvTitle;
    private RetoureStornoDialogFragment.RetoureStornoDialogListener m_listener;
    private ViewPagerRetoureStornoAdapter m_ViewPagerAdapter;
    private TabLayout m_TabLayout;
    private ViewPager m_ViewPager;
    private ListView m_listView;
    private ListViewRetoureStornoAdapter m_adapter;
    Context m_Context;
    private String m_strCategory = "";
    private String m_strProduct = "";
    private int m_iSessionTable = -1;
    private int m_iSessionBill = -1;
    private String m_strTask = "";
    private int m_iItems = 0;

    public static Fragment getInstance(String strCategory, String strProduct, int iSessionTable, int iSessionBill, String strTask) {
        Bundle bundle = new Bundle();
        bundle.putString("CATEGORY", strCategory);
        bundle.putString("PRODUCT", strProduct);
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
        m_strCategory = getArguments().getString("CATEGORY");
        m_strProduct = getArguments().getString("PRODUCT");
        m_iSessionTable = getArguments().getInt("TABLE", -1);
        m_iSessionBill = getArguments().getInt("BILL", -1);
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
        m_Context = getContext();
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

            for(ObjBillProduct objBillProduct : ObjBillProductList){
                if(objBillProduct.isChecked()){
                    if(m_strTask.equals("returned")){
                        objBillProduct.setReturned(true);
                        objBillProduct.setSqlChanged(true);
                    }
                    else{
                        objBillProduct.setCanceled(true);
                        objBillProduct.setSqlChanged(true);
                    }
                }
            }

            //set product in database
            SQLiteDatabaseHandler_TableBills db_tablebills = new SQLiteDatabaseHandler_TableBills(m_Context);
            db_tablebills.addTableBill(m_iSessionTable, m_iSessionBill);

            //show popupwindow
            showPopUpWIndowOk();
        }
    };

    public void raiseCloseDialog(){
        //update listview bill
        ((Main) getActivity()).raiseNewProduct();

        //close dialog
        Fragment mParentFragment = (RetoureStornoDialogFragment) getParentFragment();
        ((RetoureStornoDialogFragment) mParentFragment).raiseCloseDialog();
    }

    private void initListView(){
        //init adapter
        ArrayList<ObjBillProduct> lstObjBillProducts = new ArrayList<>();

        for(ObjBillProduct objBillProduct : GlobVar.g_lstTableBills.get(m_iSessionTable).get(getBillListPointer()).m_lstProducts){
            if(m_strTask.equals("returned")) {
                if(!objBillProduct.getPaid() && !objBillProduct.getReturned()){
                    lstObjBillProducts.add(objBillProduct);
                }
            }
            else{
                if(!objBillProduct.getPaid() && objBillProduct.getCanceled()){
                    lstObjBillProducts.add(objBillProduct);
                }
            }
        }
        m_adapter = new ListViewRetoureStornoAdapter(m_Context, lstObjBillProducts);
        m_listView.setAdapter(m_adapter);
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
        ObjBillProduct objBillProductReturn = new ObjBillProduct();
        for(ObjCategory objCategory: GlobVar.g_lstCategory){
            for(ObjProduct objProduct : objCategory.getListProduct()){
                for(ObjBillProduct objBillProduct : GlobVar.g_lstTableBills.get(m_iSessionTable).get(getBillListPointer()).m_lstProducts){
                    if(objProduct == objBillProduct.getProduct()){
                        objBillProductReturn = objBillProduct;
                        break;
                    }
                }
            }
        }
        return objBillProductReturn;
    }

    public void showPopUpWIndowOk() {
        FragmentManager fm = getChildFragmentManager();
        PopUpWindowOkRetoureStornoFragment popUpWindowOkFragment = PopUpWindowOkRetoureStornoFragment.newInstance();

        // pass table, bill to fragment
        Bundle args = new Bundle();

        //calculate return prize
        double dPrize = 0.00;
        for(ObjBillProduct objBillProduct : GlobVar.g_lstTableBills.get(m_iSessionTable).get(getBillListPointer()).m_lstProducts) {
            dPrize += objBillProduct.getProduct().getVK();
        }

        //returned
        if(m_strTask.equals("returned")) {

            args.putDouble("CASH", dPrize);
        }
        else{
            args.putDouble("CASH", 0.00);
        }

        popUpWindowOkFragment.setArguments(args);
        popUpWindowOkFragment.show(fm, "fragment_popupwindowok");
    }
}
