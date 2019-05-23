package fragments;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.dd.cashbox.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import adapter.ListViewAllBillAdapter;
import global.GlobVar;
import objects.ObjBill;
import objects.ObjBillProduct;

public class ViewPagerAllBillFragment extends Fragment{

    Context m_Context;
    private View m_View;
    private ExpandableListView m_listView;
    private ListViewAllBillAdapter m_listViewAllBillAdapter;
    private int m_iChoosenTable = -1;
    private String m_strChoosenDate = "";
    private String m_strTask = "";
    private String m_strTodaysDate = "";
    private Spinner m_Spinner_Tables;
    private Spinner m_Spinner_Date;
    private List<ObjBill> m_List = new ArrayList<>();

    public static Fragment getInstance(String strTask) {
        Bundle bundle = new Bundle();
        bundle.putString("TASK", strTask);
        ViewPagerAllBillFragment tabFragment = new ViewPagerAllBillFragment();
        tabFragment.setArguments(bundle);
        return tabFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //activity variables
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
        m_Spinner_Tables = view.findViewById(R.id.childfragment_allbills_spinner_tbls);
        m_Spinner_Date = view.findViewById(R.id.childfragment_allbills_spinner_date);

        //set spinner
        setSpinnerTables();
        setSpinnerDates();

        //get todaysdate
        getDate();

        //initalizie adapter
        initBillsAdapter();

        //set Listener
        m_Spinner_Tables.setOnItemSelectedListener(spinnerTablesOnItemSelectedListener);
        m_Spinner_Date.setOnItemSelectedListener(spinnerDatesOnItemSelectedListener);
    }

    //////////////////////////////////////////// LISTENER /////////////////////////////////////////////////////////////////////
    private Spinner.OnItemSelectedListener spinnerTablesOnItemSelectedListener = new Spinner.OnItemSelectedListener(){

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            m_iChoosenTable = m_Spinner_Tables.getSelectedItemPosition() - 1;
            setBillsAdapter();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            //do nothing
        }
    };

    private Spinner.OnItemSelectedListener spinnerDatesOnItemSelectedListener = new Spinner.OnItemSelectedListener(){

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            int iChoosenDate = m_Spinner_Date.getSelectedItemPosition();
            switch(iChoosenDate) {
                case 0:
                    m_strChoosenDate = "all";
                    setBillsAdapter();
                    break;
                case 1:
                    m_strChoosenDate = "today";
                    setBillsAdapter();
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            //do nothing
        }
    };

    //////////////////////////////////////////// METHODS /////////////////////////////////////////////////////////////////////

    private void setSpinnerTables(){
        //create list tables
        List<String> lstTables = new ArrayList<>();
        
        //if used as main cash register
        if(GlobVar.g_bUseMainCash){
            lstTables.add(getResources().getString(R.string.src_Hauptkasse));
        }
        else{
            lstTables.add(getResources().getString(R.string.src_AlleTische));
            for(int i = 0; i <= GlobVar.g_iTables; i++){
                int iTable = i+1;
                lstTables.add(getResources().getString(R.string.src_Tisch) + " " + iTable);
            }
        }
        
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(m_Context, android.R.layout.simple_spinner_dropdown_item, lstTables);
        m_Spinner_Tables.setAdapter(dataAdapter);

        m_Spinner_Tables.setSelection(0);
    }

    private void setSpinnerDates(){
        //create list dates
        List<String> lstDates = new ArrayList<>();
        lstDates.add(getResources().getString(R.string.src_AlleZeitraeume));
        lstDates.add(getResources().getString(R.string.src_Heute));

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(m_Context, android.R.layout.simple_spinner_dropdown_item, lstDates);
        m_Spinner_Date.setAdapter(dataAdapter);

        m_Spinner_Date.setSelection(0);
    }

    private void getDate(){
        String pattern = "dd/MM/yyyy";
        DateFormat df = new SimpleDateFormat(pattern);

        Date date = Calendar.getInstance().getTime();
        m_strTodaysDate = df.format(date);
    }

    private void initBillsAdapter(){
        m_listViewAllBillAdapter = new ListViewAllBillAdapter(m_Context, m_List);
        m_listView.setAdapter(m_listViewAllBillAdapter);

        //set bills
        setBillsAdapter();
    }

    private void setBillsAdapter(){
        if(GlobVar.g_lstTableBills.size() != 0){

            //clear list
            m_List.clear();

            //init list for adapter
            switch(m_strTask){
                case "all":
                    setBillsAll();

                    break;
                case "open":
                    setBillsOpen();

                    break;
                case "paid":
                    setBillsPaid();

                    break;
                default:
                    break;
            }

            //filter dates
            setBillsDate();

            //update adapter
            m_listViewAllBillAdapter.notifyDataSetChanged();

            //set no item description
            if(m_List.size() > 0){
                m_View.findViewById(R.id.childfragment_allbills_lv_noitem).setVisibility(View.INVISIBLE);
            }
            else{
                m_View.findViewById(R.id.childfragment_allbills_lv_noitem).setVisibility(View.VISIBLE);
            }
        }
    }

    private void setBillsAll(){
        //only one table
        if(m_iChoosenTable != -1){
            for(int iBillCounter = 0; iBillCounter < GlobVar.g_lstTableBills.get(m_iChoosenTable).size(); iBillCounter++){
                if(!GlobVar.g_lstTableBills.get(m_iChoosenTable).get(iBillCounter).m_lstProducts.isEmpty()){
                    ObjBill objBill = GlobVar.g_lstTableBills.get(m_iChoosenTable).get(iBillCounter);
                    m_List.add(objBill);
                }
            }
        }
        //all tables
        else{
            for(int iTblCounter = 0; iTblCounter < GlobVar.g_lstTableBills.size(); iTblCounter++){
                for(int iBillCounter = 0; iBillCounter < GlobVar.g_lstTableBills.get(iTblCounter).size(); iBillCounter++){
                    if(!GlobVar.g_lstTableBills.get(m_iChoosenTable).get(iBillCounter).m_lstProducts.isEmpty()){
                        ObjBill objBill = GlobVar.g_lstTableBills.get(iTblCounter).get(iBillCounter);
                        m_List.add(objBill);
                    }
                }
            }
        }
    }

    private void setBillsOpen(){
        //only one table
        if(m_iChoosenTable != -1){
            for(int iBillCounter = 0; iBillCounter < GlobVar.g_lstTableBills.get(m_iChoosenTable).size(); iBillCounter++){
                boolean bOpen = false;
                for(ObjBillProduct objBillProduct : GlobVar.g_lstTableBills.get(m_iChoosenTable).get(iBillCounter).m_lstProducts){
                    if(!objBillProduct.getPaid() && !objBillProduct.getReturned() && !objBillProduct.getCanceled()){
                        bOpen = true;
                        break;
                    }
                }
                if(bOpen){
                    if(!GlobVar.g_lstTableBills.get(m_iChoosenTable).get(iBillCounter).m_lstProducts.isEmpty()){
                        ObjBill objBill = GlobVar.g_lstTableBills.get(m_iChoosenTable).get(iBillCounter);
                        m_List.add(objBill);
                    }
                }
            }
        }
        //all tables
        else{
            for(int iTblCounter = 0; iTblCounter < GlobVar.g_lstTableBills.size(); iTblCounter++){
                for(int iBillCounter = 0; iBillCounter < GlobVar.g_lstTableBills.get(iTblCounter).size(); iBillCounter++){
                    boolean bOpen = false;
                    for(ObjBillProduct objBillProduct : GlobVar.g_lstTableBills.get(iTblCounter).get(iBillCounter).m_lstProducts){
                        if(!objBillProduct.getPaid() && !objBillProduct.getReturned() && !objBillProduct.getCanceled()){
                            bOpen = true;
                            break;
                        }
                    }
                    if(bOpen){
                        if(!GlobVar.g_lstTableBills.get(m_iChoosenTable).get(iBillCounter).m_lstProducts.isEmpty()){
                            ObjBill objBill = GlobVar.g_lstTableBills.get(iTblCounter).get(iBillCounter);
                            m_List.add(objBill);
                        }
                    }
                }
            }
        }
    }

    private void setBillsPaid(){
        //only one table
        if(m_iChoosenTable != -1){
            for(int iBillCounter = 0; iBillCounter < GlobVar.g_lstTableBills.get(m_iChoosenTable).size(); iBillCounter++){
                boolean bPaid = true;
                for(ObjBillProduct objBillProduct : GlobVar.g_lstTableBills.get(m_iChoosenTable).get(iBillCounter).m_lstProducts){
                    if(!objBillProduct.getPaid() && !objBillProduct.getReturned() && !objBillProduct.getCanceled()){
                        bPaid = false;
                        break;
                    }
                }
                if(bPaid){
                    if(!GlobVar.g_lstTableBills.get(m_iChoosenTable).get(iBillCounter).m_lstProducts.isEmpty()){
                        ObjBill objBill = GlobVar.g_lstTableBills.get(m_iChoosenTable).get(iBillCounter);
                        m_List.add(objBill);
                    }
                }
            }
        }
        //all tables
        else{
            for(int iTblCounter = 0; iTblCounter < GlobVar.g_lstTableBills.size(); iTblCounter++){
                for(int iBillCounter = 0; iBillCounter < GlobVar.g_lstTableBills.get(iTblCounter).size(); iBillCounter++){
                    boolean bPaid = true;
                    for(ObjBillProduct objBillProduct : GlobVar.g_lstTableBills.get(iTblCounter).get(iBillCounter).m_lstProducts){
                        if(!objBillProduct.getPaid() && !objBillProduct.getReturned() && !objBillProduct.getCanceled()){
                            bPaid = false;
                            break;
                        }
                    }
                    if(bPaid){
                        if(!GlobVar.g_lstTableBills.get(m_iChoosenTable).get(iBillCounter).m_lstProducts.isEmpty()){
                            ObjBill objBill = GlobVar.g_lstTableBills.get(iTblCounter).get(iBillCounter);
                            m_List.add(objBill);
                        }
                    }
                }
            }
        }
    }

    private void setBillsDate(){
        //init list for adapter
        switch(m_strChoosenDate){
            case "all":
                //do nothing

                break;
            case "today":
                //delete items
                for(int i = m_List.size(); i-- > 0;) {
                    String strBillingDate = m_List.get(i).getBillingDate();
                    strBillingDate = strBillingDate.substring(0, 10);
                    if(!strBillingDate.equals(m_strTodaysDate)){
                        m_List.remove(i);
                    }
                }

                break;
            default:
                break;
        }
    }
}
