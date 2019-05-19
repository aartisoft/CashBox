package fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.dd.cashbox.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import adapter.ListViewCashPositionAdapter;
import global.GlobVar;
import objects.ObjBillProduct;
import objects.ObjCategory;
import objects.ObjProduct;

import static global.GlobVar.g_lstTableBills;

public class CurrCashPosTabProductFragment extends Fragment {
    private ListViewCashPositionAdapter m_adapterlvsumincome;
    private ListView m_lvsumincome;
    private Context m_Context;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)    {
        View  view = inflater.inflate(R.layout.fragment_currcashpostabcategory, container, false);

        //init variables
        m_Context = view.getContext();
        m_lvsumincome = view.findViewById(R.id.fragment_currcashpostabcategorylv);

        //set listview
        setListViewProducts();

        return view;
    }

    private void setListViewProducts(){
        //build data for listview
        List lstViewAttr = new ArrayList<>();
        DecimalFormat df = new DecimalFormat("0.00");
        HashMap<String,String> hashMap = new HashMap<>();


        for(int iCounterCategory = 0; iCounterCategory < GlobVar.g_lstCategory.size(); iCounterCategory++){
            for(ObjProduct objProduct : GlobVar.g_lstCategory.get(iCounterCategory).getListProduct()){
                hashMap = new HashMap<>();
                hashMap.put("typ", objProduct.getName());
                //get sum
                double dSum = getProductSum(objProduct.getName());
                String strOutput = df.format(dSum);
                hashMap.put("value", strOutput + " €");
                lstViewAttr.add(hashMap);
            }
        }

        m_adapterlvsumincome = new ListViewCashPositionAdapter(lstViewAttr);
        m_lvsumincome.setAdapter(m_adapterlvsumincome);
    }

    private double getProductSum(String p_strProduct){
        double dSum = 0.0;
        for(int iCounterTables = 0; iCounterTables < g_lstTableBills.size(); iCounterTables++){
            for(int iCounterBills = 0; iCounterBills < g_lstTableBills.get(iCounterTables).size(); iCounterBills++){
                for(ObjBillProduct objBillProduct : g_lstTableBills.get(iCounterTables).get(iCounterBills).m_lstProducts){
                    if(objBillProduct.getProduct().getName().equals(p_strProduct)){
                        dSum += objBillProduct.getVK();
                    }
                }
            }
        }
        return dSum;
    }
}