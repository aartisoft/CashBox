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
import java.util.List;

import adapter.ListViewCurrCashPositionProductAdapter;
import global.GlobVar;
import objects.ObjBillProduct;
import objects.ObjProduct;
import objects.ObjProductCashPos;


public class CurrCashPosTabProductFragment extends Fragment {
    private ListViewCurrCashPositionProductAdapter m_adapterlvsumincome;
    private ListView m_lvsumincome;
    private Context m_Context;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)    {
        View  view = inflater.inflate(R.layout.fragment_currcashpostabproduct, container, false);

        //init variables
        m_Context = view.getContext();
        m_lvsumincome = view.findViewById(R.id.fragment_currcashpostabproductlv);

        //set listview
        setListViewProducts();

        return view;
    }

    private void setListViewProducts(){
        //build data for listview
        List lstViewAttr = new ArrayList<>();
        List<ObjProductCashPos> lstProduct = new ArrayList<>();


        for(int iCounterCategory = 0; iCounterCategory < GlobVar.g_lstCategory.size(); iCounterCategory++){
            for(ObjProduct objProduct : GlobVar.g_lstCategory.get(iCounterCategory).getListProduct()){
                //init object regular
                ObjProductCashPos objProductCashPos = new ObjProductCashPos();
                objProductCashPos.setName(objProduct.getName());
                objProductCashPos = getProductInfoTax(objProductCashPos, objProduct.getTax(), false);
                if(objProductCashPos != null){
                    lstProduct.add(objProductCashPos);
                }

                //init object togo
                objProductCashPos = new ObjProductCashPos();
                objProductCashPos.setName(objProduct.getName());
                objProductCashPos = getProductInfoTax(objProductCashPos, objProduct.getTax(), true);
                if(objProductCashPos != null){
                    lstProduct.add(objProductCashPos);
                }

                //init object retoure
                objProductCashPos = new ObjProductCashPos();
                objProductCashPos.setName(objProduct.getName());
                objProductCashPos = getProductInfoRetoure(objProductCashPos);
                if(objProductCashPos != null){
                    lstProduct.add(objProductCashPos);
                }

                //init object storno
                objProductCashPos = new ObjProductCashPos();
                objProductCashPos.setName(objProduct.getName());
                objProductCashPos = getProductInfoStorno(objProductCashPos);
                if(objProductCashPos != null){
                    lstProduct.add(objProductCashPos);
                }
            }
        }

        m_adapterlvsumincome = new ListViewCurrCashPositionProductAdapter(lstProduct);
        m_lvsumincome.setAdapter(m_adapterlvsumincome);
    }

    private ObjProductCashPos getProductInfoTax(ObjProductCashPos p_objProductCashPos, double p_dTax, boolean p_bToGo){
        double dSum = 0.0;
        int iCount = 0;
        DecimalFormat df = new DecimalFormat("0.00");

        for(int iCounterTables = 0; iCounterTables < GlobVar.g_lstTables.size(); iCounterTables++) {
            for (int iCounterBills = 0; iCounterBills < GlobVar.g_lstTables.get(iCounterTables).g_lstBills.size(); iCounterBills++) {
                for (ObjBillProduct objBillProduct : GlobVar.g_lstTables.get(iCounterTables).g_lstBills.get(iCounterBills).m_lstProducts) {
                    //if regular
                    if (!p_bToGo) {
                        if (objBillProduct.getProduct().getName().equals(p_objProductCashPos.getName())
                                && !objBillProduct.getToGo() && objBillProduct.getPaid() && !objBillProduct.getReturned()) {
                            dSum += objBillProduct.getVK();
                            iCount++;
                        }
                    }
                    //if togo
                    else {
                        if (objBillProduct.getProduct().getName().equals(p_objProductCashPos.getName())
                                && objBillProduct.getToGo() && objBillProduct.getPaid() && !objBillProduct.getReturned()) {
                            dSum += objBillProduct.getVK();
                            p_dTax = 7.0;
                            iCount++;
                        }
                    }
                }
            }
        }

        if(iCount != 0){
            //set sum
            String strOutput = df.format(dSum);
            p_objProductCashPos.setVK(strOutput + " €");

            //set info
            String strInfo;
            if(iCount > 1){
                strInfo = iCount + " " + m_Context.getResources().getString(R.string.src_VerkaeufeMit)
                                    + " " + p_dTax + "%" + " " + m_Context.getResources().getString(R.string.src_MwSt);
            }
            else{
                strInfo = iCount + " " + m_Context.getResources().getString(R.string.src_VerkaufMit)
                        + " " + p_dTax + "%" + " " + m_Context.getResources().getString(R.string.src_MwSt);
            }


            p_objProductCashPos.setInfo(strInfo);

            return p_objProductCashPos;
        }
        return null;
    }

    private ObjProductCashPos getProductInfoRetoure(ObjProductCashPos p_objProductCashPos){
        double dSum = 0.0;
        int iCount = 0;
        DecimalFormat df = new DecimalFormat("0.00");

        for(int iCounterTables = 0; iCounterTables < GlobVar.g_lstTables.size(); iCounterTables++) {
            for (int iCounterBills = 0; iCounterBills < GlobVar.g_lstTables.get(iCounterTables).g_lstBills.size(); iCounterBills++) {
                for (ObjBillProduct objBillProduct : GlobVar.g_lstTables.get(iCounterTables).g_lstBills.get(iCounterBills).m_lstProducts) {
                    if (objBillProduct.getProduct().getName().equals(p_objProductCashPos.getName())
                            && objBillProduct.getReturned()) {
                        dSum += objBillProduct.getVK();
                        iCount++;
                    }
                }
            }
        }

        if(iCount != 0){
            //set sum
            String strOutput = df.format(dSum);
            p_objProductCashPos.setVK("-" + strOutput + " €");

            //set info
            String strInfo;
            if(iCount > 1){
                strInfo = iCount + " " + m_Context.getResources().getString(R.string.src_Retouren);
            }
            else{
                strInfo = iCount + " " + m_Context.getResources().getString(R.string.src_Retoure);
            }
            p_objProductCashPos.setInfo(strInfo);

            return p_objProductCashPos;
        }
        return null;
    }

    private ObjProductCashPos getProductInfoStorno(ObjProductCashPos p_objProductCashPos){
        double dSum = 0.0;
        int iCount = 0;
        DecimalFormat df = new DecimalFormat("0.00");

        for(int iCounterTables = 0; iCounterTables < GlobVar.g_lstTables.size(); iCounterTables++) {
            for (int iCounterBills = 0; iCounterBills < GlobVar.g_lstTables.get(iCounterTables).g_lstBills.size(); iCounterBills++) {
                for (ObjBillProduct objBillProduct : GlobVar.g_lstTables.get(iCounterTables).g_lstBills.get(iCounterBills).m_lstProducts) {
                    if (objBillProduct.getProduct().getName().equals(p_objProductCashPos.getName())
                            && objBillProduct.getCanceled()) {
                        dSum += objBillProduct.getVK();
                        iCount++;
                    }
                }
            }
        }

        if(iCount != 0){
            //set sum
            String strOutput = df.format(dSum);
            //p_objProductCashPos.setVK("-" + strOutput + " €");
            p_objProductCashPos.setVK("0,00€");

            //set info
            String strInfo;
            if(iCount > 1){
                strInfo = iCount + " " + m_Context.getResources().getString(R.string.src_Stornierungen);
            }
            else{
                strInfo = iCount + " " + m_Context.getResources().getString(R.string.src_Stornierung);
            }

            p_objProductCashPos.setInfo(strInfo);

            return p_objProductCashPos;
        }
        return null;
    }
}