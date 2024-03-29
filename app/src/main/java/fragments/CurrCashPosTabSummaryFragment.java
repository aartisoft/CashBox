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

import static global.GlobVar.g_lstTables;

public class CurrCashPosTabSummaryFragment extends Fragment {
    private ListViewCurrCashPositionAdapter m_adapterlvsumincome;
    private ListViewCurrCashPositionAdapter m_adapterlvsumoincome;
    private ListView m_lvsumincome;
    private ListView m_lvsumoincome;
    private Context m_Context;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)    {
        View  view = inflater.inflate(R.layout.fragment_currcashpostabsummary, container, false);

        //init variables
        m_Context = view.getContext();
        m_lvsumincome = view.findViewById(R.id.fragment_currcashpostabsummary_lvincome);
        m_lvsumoincome = view.findViewById(R.id.fragment_currcashpostabsummary_lvoincome);

        //set listview
        setListViewIncome();
        setListViewOtherIncome();


        return view;
    }

    private void setListViewIncome(){
        //build data for listview
        List lstViewAttr = new ArrayList<>();
        DecimalFormat df = new DecimalFormat("0.00");
        HashMap<String,String> hashMap = new HashMap<>();

        hashMap.put("typ", getResources().getString(R.string.src_Start));
        String strDate = getStartingDate();
        hashMap.put("value", strDate);
        lstViewAttr.add(hashMap);

        hashMap = new HashMap<>();
        hashMap.put("typ", getResources().getString(R.string.src_Barzahlungen));
        //get sum
        double dSum = getAllIncomeSum();
        String strOutput = df.format(dSum);
        hashMap.put("value", strOutput + " €");
        lstViewAttr.add(hashMap);

        hashMap = new HashMap<>();
        hashMap.put("typ", getResources().getString(R.string.src_Retoure));
        //get sum
        dSum = getRetoureSum();
        strOutput = df.format(dSum);
        hashMap.put("value", "-" + strOutput + " €");
        lstViewAttr.add(hashMap);

        hashMap = new HashMap<>();
        hashMap.put("typ", getResources().getString(R.string.src_Gesamt));
        //get sum
        double dSumAll = getRevenueSum();
        String strSumAll = df.format(dSumAll);
        hashMap.put("value", strSumAll + " €");
        lstViewAttr.add(hashMap);

        m_adapterlvsumincome = new ListViewCurrCashPositionAdapter(lstViewAttr);
        m_lvsumincome.setAdapter(m_adapterlvsumincome);
    }

    private void setListViewOtherIncome(){
        //build data for listview
        List lstViewAttr = new ArrayList<>();
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("typ", getResources().getString(R.string.src_Trinkgeld));

        //get sum
        double dSum = getAllTip();
        DecimalFormat df = new DecimalFormat("0.00");
        String strOutput = df.format(dSum);
        hashMap.put("value", strOutput + " €");
        lstViewAttr.add(hashMap);

        /*hashMap = new HashMap<>();
        hashMap.put("typ", getResources().getString(R.string.src_DruckerName));
        hashMap.put("value", printer.getDeviceName());
        lstViewAttr.add(hashMap);*/

        m_adapterlvsumoincome = new ListViewCurrCashPositionAdapter(lstViewAttr);
        m_lvsumoincome.setAdapter(m_adapterlvsumoincome);
    }

    private String getStartingDate(){
        String strDate = "";
        for(int iCounterTables = 0; iCounterTables < g_lstTables.size(); iCounterTables++){
            for(int iCounterBills = 0; iCounterBills < g_lstTables.get(iCounterTables).g_lstBills.size(); iCounterBills++){
                if(g_lstTables.get(iCounterTables).g_lstBills.get(iCounterBills).getBillNr() == 1){
                    strDate = GlobVar.g_lstTables.get(iCounterTables).g_lstBills.get(iCounterBills).getBillingDate();
                    return strDate;
                }
            }
        }
        return strDate;
    }

    private double getAllIncomeSum(){
        double dSum = 0.0;
        for(int iCounterTables = 0; iCounterTables < g_lstTables.size(); iCounterTables++){
            for(int iCounterBills = 0; iCounterBills < g_lstTables.get(iCounterTables).g_lstBills.size(); iCounterBills++){
                for(ObjBillProduct objBillProduct : g_lstTables.get(iCounterTables).g_lstBills.get(iCounterBills).m_lstProducts){
                    if(objBillProduct.getPaid()){
                        dSum += objBillProduct.getVK();
                    }
                }
            }
        }
        return dSum;
    }

    private double getRevenueSum(){
        double dSum = 0.0;
        for(int iCounterTables = 0; iCounterTables < g_lstTables.size(); iCounterTables++){
            for(int iCounterBills = 0; iCounterBills < g_lstTables.get(iCounterTables).g_lstBills.size(); iCounterBills++){
                for(ObjBillProduct objBillProduct : g_lstTables.get(iCounterTables).g_lstBills.get(iCounterBills).m_lstProducts){
                    if(objBillProduct.getPaid() && !objBillProduct.getReturned()){
                        dSum += objBillProduct.getVK();
                    }
                }
            }
        }
        return dSum;
    }

    private double getRetoureSum(){
        double dSum = 0.0;
        for(int iCounterTables = 0; iCounterTables < g_lstTables.size(); iCounterTables++){
            for(int iCounterBills = 0; iCounterBills < g_lstTables.get(iCounterTables).g_lstBills.size(); iCounterBills++){
                for(ObjBillProduct objBillProduct : g_lstTables.get(iCounterTables).g_lstBills.get(iCounterBills).m_lstProducts){
                    if(objBillProduct.getReturned() && objBillProduct.getPaid()){
                        dSum += objBillProduct.getVK();
                    }
                }
            }
        }
        return dSum;
    }

    private double getAllTip(){
        double dSum = 0.0;
        for(int iCounterTables = 0; iCounterTables < g_lstTables.size(); iCounterTables++){
            for(int iCounterBills = 0; iCounterBills < g_lstTables.get(iCounterTables).g_lstBills.size(); iCounterBills++){
                dSum += g_lstTables.get(iCounterTables).g_lstBills.get(iCounterBills).getTip();
            }
        }
        return dSum;
    }
}