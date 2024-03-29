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

import adapter.ListViewCurrCashPositionAdapter;
import global.GlobVar;
import objects.ObjBillProduct;
import objects.ObjCategory;

import static global.GlobVar.g_lstTables;

public class CurrCashPosTabCategoryFragment extends Fragment {
    private ListViewCurrCashPositionAdapter m_adapterlvsumincome;
    private ListViewCurrCashPositionAdapter m_adapterlvsumoincome;
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
        setListViewCategories();

        return view;
    }

    private void setListViewCategories(){
        //build data for listview
        List lstViewAttr = new ArrayList<>();
        DecimalFormat df = new DecimalFormat("0.00");
        HashMap<String,String> hashMap = new HashMap<>();


        for(ObjCategory objCategory : GlobVar.g_lstCategory){
            hashMap = new HashMap<>();
            hashMap.put("typ", objCategory.getName());
            //get sum
            double dSum = getCategorySum(objCategory.getName());
            String strOutput = df.format(dSum);
            hashMap.put("value", strOutput + " €");
            lstViewAttr.add(hashMap);
        }

        m_adapterlvsumincome = new ListViewCurrCashPositionAdapter(lstViewAttr);
        m_lvsumincome.setAdapter(m_adapterlvsumincome);
    }

    private double getCategorySum(String p_strCategory){
        double dSum = 0.0;
        for(int iCounterTables = 0; iCounterTables < g_lstTables.size(); iCounterTables++){
            for(int iCounterBills = 0; iCounterBills < g_lstTables.get(iCounterTables).g_lstBills.size(); iCounterBills++){
                for(ObjBillProduct objBillProduct : g_lstTables.get(iCounterTables).g_lstBills.get(iCounterBills).m_lstProducts){
                    if(objBillProduct.getCategory().equals(p_strCategory) && objBillProduct.getPaid()
                        && !objBillProduct.getReturned() && !objBillProduct.getCanceled()){
                        dSum += objBillProduct.getVK();
                    }
                }
            }
        }
        return dSum;
    }
}