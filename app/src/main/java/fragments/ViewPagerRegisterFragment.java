package fragments;


import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.dd.cashbox.Main;
import com.example.dd.cashbox.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import adapter.GridViewProductAdapter;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import global.GlobVar;
import objects.ObjBill;
import objects.ObjBillProduct;
import objects.ObjProduct;

public class ViewPagerRegisterFragment extends Fragment {

    int m_position;
    private GridView m_GridView;
    private int m_iSessionTable = -1;
    private int m_iSessionBill = -1;
    private List<ObjProduct> m_lstProductsAdapter = new ArrayList<>();
    private GridViewProductAdapter m_gridViewProductAdapter;

    public static Fragment getInstance(int position) {
        Bundle bundle = new Bundle();
        bundle.putInt("pos", position);
        ViewPagerRegisterFragment tabFragment = new ViewPagerRegisterFragment();
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
        m_GridView = view.findViewById(R.id.fragment_tagregister_gridview);


        //set productlistadapter
        m_lstProductsAdapter = new ArrayList<>();
        for(ObjProduct objProduct : GlobVar.g_lstCategory.get(m_position).getListProduct()){
            if(objProduct.getEnabled()){
                m_lstProductsAdapter.add(objProduct);
            }
        }

        m_gridViewProductAdapter = new GridViewProductAdapter(getActivity().getApplicationContext(),
                                    m_lstProductsAdapter, GlobVar.g_lstCategory.get(m_position).getProdColor());
        m_GridView.setAdapter(m_gridViewProductAdapter);

        //set Listener
        m_GridView.setOnItemClickListener(gvTableOnItemClickListener);
        m_GridView.setOnItemLongClickListener(gvTableOnItemLongClickListener);
    }

    private AdapterView.OnItemClickListener gvTableOnItemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            m_iSessionTable = ((Main) getActivity()).getVarTable();
            m_iSessionBill = ((Main) getActivity()).getVarBill();
            ObjProduct objproduct = m_gridViewProductAdapter.getItem(position);


            // fragment only available if bill has been choosen
            if(m_iSessionBill != -1){
                //bill closed?
                if(!GlobVar.g_lstTables.get(m_iSessionTable).g_lstBills.get(getBillListPointer()).getClosed()){
                    writeTableBillsList(position, m_iSessionTable, m_iSessionBill);
                    //tel main activity there is a new product available
                    ((Main) getActivity()).raiseNewProduct();
                }
                else{
                    Toast.makeText(view.getContext(), getResources().getString(R.string.src_BelegBereitsGeschlossen), Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(view.getContext(), getResources().getString(R.string.src_KeinBelegAusgewaehlt), Toast.LENGTH_SHORT).show();
            }
        }
    };

    private AdapterView.OnItemLongClickListener gvTableOnItemLongClickListener = new AdapterView.OnItemLongClickListener() {

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            m_iSessionTable = ((Main) getActivity()).getVarTable();
            m_iSessionBill = ((Main) getActivity()).getVarBill();
            ObjProduct objproduct = m_gridViewProductAdapter.getItem(position);


            // fragment only available if bill has been choosen
            if(m_iSessionBill != -1){
                //bill closed?
                if(!GlobVar.g_lstTables.get(m_iSessionTable).g_lstBills.get(getBillListPointer()).getClosed()){
                    FragmentManager fm = getChildFragmentManager();
                    RegisterPopUpDialogFragment registerPopUpDialogFragment = RegisterPopUpDialogFragment.newInstance("Register PopUp");

                    // pass table, bill to fragment
                    Bundle args = new Bundle();
                    args.putInt("TABLE", m_iSessionTable);
                    args.putInt("BILL", m_iSessionBill);
                    args.putString("CATEGORY", objproduct.getCategory());
                    args.putString("PRODUCT", objproduct.getName());

                    registerPopUpDialogFragment.setArguments(args);
                    registerPopUpDialogFragment.show(fm, "fragment_registerpopup");
                }
                else{
                    Toast.makeText(view.getContext(), getResources().getString(R.string.src_BelegBereitsGeschlossen), Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(view.getContext(), getResources().getString(R.string.src_KeinBelegAusgewaehlt), Toast.LENGTH_SHORT).show();
            }

            return true;
        }
    };

    private void writeTableBillsList(int p_iPosition, int iTable, int iBillNr){

        //get bill
        int iBill = 0;
        for(ObjBill objBill : GlobVar.g_lstTables.get(iTable).g_lstBills){
            if(objBill.getBillNr() == iBillNr){
                break;
            }
            iBill++;
        }

        ObjProduct objproduct = m_gridViewProductAdapter.getItem(p_iPosition);

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
        objbillproduct.setVK(objproduct.getVK());
        objbillproduct.setCategory(objproduct.getCategory());

        //add globally
        GlobVar.g_lstTables.get(iTable).g_lstBills.get(iBill).m_lstProducts.add(objbillproduct);
    }

    private int getBillListPointer(){
        //get bill
        int iBill = 0;
        for(ObjBill objBill : GlobVar.g_lstTables.get(m_iSessionTable).g_lstBills){
            if(objBill.getBillNr() == m_iSessionBill){
                return iBill;
            }
            iBill++;
        }
        return 0;
    }
}
