package fragments;


import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.dd.cashbox.Main;
import com.example.dd.cashbox.R;

import java.util.ArrayList;

import adapter.GridViewProductAdapter;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import global.GlobVar;
import objects.ObjBill;
import objects.ObjBillProduct;
import objects.ObjCategory;
import objects.ObjPrinter;
import objects.ObjProduct;

public class ViewPagerRegisterFragment extends Fragment {

    int m_position;
    private GridView m_GridView;
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

        m_gridViewProductAdapter = new GridViewProductAdapter(getActivity().getApplicationContext(),
                                    GlobVar.g_lstCategory.get(m_position).getListProduct(), GlobVar.g_lstCategory.get(m_position).getProdColor());
        m_GridView.setAdapter(m_gridViewProductAdapter);

        //set Listener
        m_GridView.setOnItemClickListener(gvTableOnItemClickListener);
    }

    private AdapterView.OnItemClickListener gvTableOnItemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            writeTableBillsList(position);

            //tel main activity there is a new product available
            ((Main) getActivity()).raiseNewProduct();
        }
    };

    private void writeTableBillsList(int p_iPosition){
        int iTable = ((Main) getActivity()).getVarTable();
        int iBillNr = ((Main) getActivity()).getVarBill();

        //get bill
        int iBill = 0;
        for(ObjBill objBill : GlobVar.g_lstTableBills.get(iTable)){
            if(objBill.getBillNr() == iBillNr){
                break;
            }
            iBill++;
        }


        ObjProduct objproduct = m_gridViewProductAdapter.getItem(p_iPosition);

        boolean bProductExists = false;
        int iProductPos = 0;
        if(GlobVar.g_lstTableBills.get(iTable).get(iBill).m_lstProducts != null){
            for(ObjBillProduct objbillproduct : GlobVar.g_lstTableBills.get(iTable).get(iBill).m_lstProducts){
                if(objbillproduct.getProduct().getName().equals(objproduct.getName())){
                    bProductExists = true;
                    break;
                }
                iProductPos++;
            }
        }
        else{
            GlobVar.g_lstTableBills.get(iTable).get(iBill).m_lstProducts = new ArrayList<ObjBillProduct>();
        }


        //if product is not in list
        if(!bProductExists){
            ObjBillProduct objbillproduct = new ObjBillProduct();
            objbillproduct.setProduct(objproduct);
            objbillproduct.setQuantity(1);
            objbillproduct.setCategory(objproduct.getCategory());

            //get printer
            for(ObjCategory objCategory : GlobVar.g_lstCategory){
                for (ObjPrinter objPrinter : GlobVar.g_lstPrinter) {
                    if(objCategory.getPrinter().getMacAddress().equals(objPrinter.getMacAddress())){
                        objbillproduct.setPrinter(objPrinter);
                    }
                }
            }


            GlobVar.g_lstTableBills.get(iTable).get(iBill).m_lstProducts.add(objbillproduct);
        }
        //if product is already in list
        else{
            int iQuantity = GlobVar.g_lstTableBills.get(iTable).get(iBill).m_lstProducts.get(iProductPos).getQuantity();
            iQuantity++;
            GlobVar.g_lstTableBills.get(iTable).get(iBill).m_lstProducts.get(iProductPos).setQuantity(iQuantity);

            //sql database has to update
            GlobVar.g_lstTableBills.get(iTable).get(iBill).m_lstProducts.get(iProductPos).setSqlChanged(true);
        }
    }
}
