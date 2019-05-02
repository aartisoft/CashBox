package fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.dd.cashbox.AllBills;
import com.example.dd.cashbox.Main;
import com.example.dd.cashbox.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import SQLite.SQLiteDatabaseHandler_TableBills;
import adapter.ListViewAllBillAdapter;
import adapter.ListViewRetoureStornoAdapter;
import global.GlobVar;
import objects.ObjBill;
import objects.ObjBillProduct;

public class ViewPagerAllBillFragment extends Fragment{

    Context m_Context;
    private View m_View;
    private ExpandableListView m_listView;
    private ListViewAllBillAdapter m_listViewAllBillAdapter;
    private int m_iSessionTable = -1;
    private String m_strSessionDate = "";
    private String m_strTask = "";
    private double m_dPrize = 0.00;

    public static Fragment getInstance(int iSessionTable, String strSessionDate, String strTask) {
        Bundle bundle = new Bundle();
        bundle.putInt("TABLE", iSessionTable);
        bundle.putString("DATE", strSessionDate);
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
        m_strSessionDate = getArguments().getString("DATE");
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
        m_listView = view.findViewById(R.id.childfragment_allbills_elv);

        //set bills
        setBills();

        //set Listener

    }

    //////////////////////////////////////////// LISTENER /////////////////////////////////////////////////////////////////////


    //////////////////////////////////////////// METHODS /////////////////////////////////////////////////////////////////////

    private void setBills(){
        if(GlobVar.g_lstTableBills.size() != 0){

            //init list for adapter
            List<ObjBill> m_List = new ArrayList<>();

            switch(m_strTask){
                case "all":
                    for(int iTblCounter = 0; iTblCounter < GlobVar.g_lstTableBills.size(); iTblCounter++){
                        for(int iBillCounter = 0; iBillCounter < GlobVar.g_lstTableBills.get(iTblCounter).size(); iBillCounter++){
                            ObjBill objBill = GlobVar.g_lstTableBills.get(iTblCounter).get(iBillCounter);
                            m_List.add(objBill);
                        }
                    }

                    break;
                case "open":
                    for(int iTblCounter = 0; iTblCounter < GlobVar.g_lstTableBills.size(); iTblCounter++){
                        for(int iBillCounter = 0; iBillCounter < GlobVar.g_lstTableBills.get(iTblCounter).size(); iBillCounter++){
                            boolean bOpen = true;
                            for(ObjBillProduct objBillProduct : GlobVar.g_lstTableBills.get(iTblCounter).get(iBillCounter).m_lstProducts){
                                if(objBillProduct.getPaid()){
                                    bOpen = false;
                                    break;
                                }
                            }
                            if(bOpen){
                                ObjBill objBill = GlobVar.g_lstTableBills.get(iTblCounter).get(iBillCounter);
                                m_List.add(objBill);
                            }
                        }
                    }

                    break;
                case "paid":
                    for(int iTblCounter = 0; iTblCounter < GlobVar.g_lstTableBills.size(); iTblCounter++){
                        for(int iBillCounter = 0; iBillCounter < GlobVar.g_lstTableBills.get(iTblCounter).size(); iBillCounter++){
                            boolean bPaid = true;
                            for(ObjBillProduct objBillProduct : GlobVar.g_lstTableBills.get(iTblCounter).get(iBillCounter).m_lstProducts){
                                if(!objBillProduct.getPaid()){
                                    bPaid = false;
                                    break;
                                }
                            }
                            if(bPaid){
                                ObjBill objBill = GlobVar.g_lstTableBills.get(iTblCounter).get(iBillCounter);
                                m_List.add(objBill);
                            }
                        }
                    }
                    break;
                default:
                    break;
            }

            m_listViewAllBillAdapter = new ListViewAllBillAdapter(m_Context, m_List);
            m_listView.setAdapter(m_listViewAllBillAdapter);

            //set no item description
            if(m_List.size() > 0){
                m_View.findViewById(R.id.childfragment_allbills_lv_noitem).setVisibility(View.INVISIBLE);
            }
            else{
                m_View.findViewById(R.id.childfragment_allbills_lv_noitem).setVisibility(View.VISIBLE);
            }
        }
    }

    private void setNoItem(){

    }
}
