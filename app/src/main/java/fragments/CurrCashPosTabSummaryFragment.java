package fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.dd.cashbox.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import adapter.ListViewCashPositionAdapter;
import adapter.ListViewPrinterDetailAdapter;
import global.GlobVar;
import objects.ObjPrinter;

public class CurrCashPosTabSummaryFragment extends Fragment {
    private ListViewCashPositionAdapter m_adapterlvsumincome;
    private ListViewCashPositionAdapter m_adapterlvsumoincome;
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
        m_lvsumincome = view.findViewById(R.id.fragment_currcashpostabsummary_lvoincome);

        //set listview
        setListViewIncome();
        setListViewOtherIncome();


        return view;
    }

    private void setListViewIncome(){
        //build data for listview
        List lstViewAttr = new ArrayList<>();
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("typ", getResources().getString(R.string.src_Barzahlungen));
        hashMap.put("value", "20,00 €");
        lstViewAttr.add(hashMap);

        /*hashMap = new HashMap<>();
        hashMap.put("typ", getResources().getString(R.string.src_DruckerName));
        hashMap.put("value", printer.getDeviceName());
        lstViewAttr.add(hashMap);*/

        m_adapterlvsumincome = new ListViewCashPositionAdapter(m_Context, lstViewAttr);
        m_lvsumincome.setAdapter(m_adapterlvsumincome);
    }

    private void setListViewOtherIncome(){
        //build data for listview
        List lstViewAttr = new ArrayList<>();
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("typ", getResources().getString(R.string.src_Trinkgeld));
        hashMap.put("value", "10,00 €");
        lstViewAttr.add(hashMap);

        /*hashMap = new HashMap<>();
        hashMap.put("typ", getResources().getString(R.string.src_DruckerName));
        hashMap.put("value", printer.getDeviceName());
        lstViewAttr.add(hashMap);*/

        m_adapterlvsumoincome = new ListViewCashPositionAdapter(m_Context, lstViewAttr);
        m_lvsumoincome.setAdapter(m_adapterlvsumoincome);
    }
}