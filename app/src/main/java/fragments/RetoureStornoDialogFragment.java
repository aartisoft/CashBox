package fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.example.dd.cashbox.R;
import com.google.android.material.tabs.TabLayout;

import adapter.ViewPagerRetoureStornoAdapter;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;
import global.GlobVar;
import objects.ObjBill;
import objects.ObjBillProduct;

public class RetoureStornoDialogFragment extends DialogFragment {

    private RetoureStornoDialogListener m_listener;
    private ViewPagerRetoureStornoAdapter m_ViewPagerAdapter;
    private TabLayout m_TabLayout;
    private ViewPager m_ViewPager;
    private String m_strCategory = "";
    private String m_strProduct = "";
    private int m_iSessionTable = -1;
    private int m_iSessionBill = -1;
    private int m_iReturned = 0;
    private FragmentActivity m_Context;
    private static RetoureStornoDialogFragment m_frag;

    public RetoureStornoDialogFragment() {
    }

    public interface RetoureStornoDialogListener {
        void onFinishRetoureStornoDialog();
    }

    public static RetoureStornoDialogFragment newInstance() {
        m_frag = new RetoureStornoDialogFragment();
        Bundle args = new Bundle();
        m_frag.setArguments(args);
        return m_frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_retourestorno, container, false);

        //activity variables
        m_strCategory = getArguments().getString("CATEGORY");
        m_strProduct = getArguments().getString("PRODUCT");
        m_iSessionTable = getArguments().getInt("TABLE", -1);
        m_iSessionBill = getArguments().getInt("BILL", -1);

        //set UI
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        Toolbar toolbar = view.findViewById(R.id.fragment_retourestorno_tb);
        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);
        toolbar.setNavigationOnClickListener(tbOnClickListener);

        //set variables
        m_Context = getActivity();
        m_TabLayout = view.findViewById(R.id.fragment_retourestorno_tab);
        m_ViewPager = view.findViewById(R.id.fragment_retourestorno_viewpager);

        //set Tab
        setTabulator();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    View.OnClickListener tbOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            m_frag.dismiss();
        }
    };

    public void raiseCloseDialog(){
        m_frag.dismiss();
    }


    private void setTabulator(){
        //setup viewpager
        m_ViewPagerAdapter = new ViewPagerRetoureStornoAdapter(getChildFragmentManager());
        m_ViewPagerAdapter.addFragment(new ViewPagerRetoureStornoFragment().getInstance(m_strCategory, m_strProduct, m_iSessionTable, m_iSessionBill, "canceled")
                                        , getResources().getString(R.string.src_Storno), 1, getContext());
        m_ViewPagerAdapter.addFragment(new ViewPagerRetoureStornoFragment().getInstance(m_strCategory, m_strProduct, m_iSessionTable, m_iSessionBill, "returned")
                , getResources().getString(R.string.src_Retoure), 1, getContext());

        m_ViewPager.setAdapter(m_ViewPagerAdapter);

        //setup custom view
        m_TabLayout.setupWithViewPager(m_ViewPager);
        m_TabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        View headerView = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.fragment_retourestorno_tablayout, null, false);

        //set for all tabs
        for(int tabs = 0; tabs < m_TabLayout.getTabCount(); tabs++){
            m_TabLayout.getTabAt(tabs).setCustomView(m_ViewPagerAdapter.getTabView(tabs));
        }
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

    private ObjBillProduct getObjBillProduct(){
        return GlobVar.g_lstTables.get(m_iSessionTable).g_lstBills.get(getBillListPointer()).m_lstProducts.get(0);
    }
}
