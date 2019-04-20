package fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.dd.cashbox.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import adapter.ViewPagerRetoureStornoAdapter;
import global.GlobVar;
import objects.ObjBill;
import objects.ObjBillProduct;

public class RegisterPopUpDialogFragment extends DialogFragment implements View.OnClickListener {

    private String m_strCategory = "";
    private String m_strProduct = "";
    private Button m_button_min;
    private Button m_button_pl;
    private EditText m_edttCount;
    private EditText m_edtInfo;
    private EditText m_edtVK;
    private SwitchCompat m_Switch;
    private FloatingActionButton m_fab;

    private int m_iItems = 0;
    private FragmentActivity m_Context;
    private static RegisterPopUpDialogFragment m_frag;

    public RegisterPopUpDialogFragment() {
    }

    public static RegisterPopUpDialogFragment newInstance(String title) {
        m_frag = new RegisterPopUpDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        m_frag.setArguments(args);
        return m_frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_registerpopup, container, false);

        //activity variables
        m_strCategory = getArguments().getString("CATEGORY");
        m_strProduct = getArguments().getString("PRODUCT");

        //set UI
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        Toolbar toolbar = view.findViewById(R.id.fragment_registerpopup_tb);
        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);
        toolbar.setNavigationOnClickListener(tbOnClickListener);

        //set variables
        m_Context = getActivity();
        m_button_min = view.findViewById(R.id.fragment_registerpopup_page_btnminus);
        m_button_pl = view.findViewById(R.id.fragment_registerpopup_page_btnplus);
        m_edttCount = view.findViewById(R.id.fragment_registerpopup_page_edttxt);
        m_edtInfo= view.findViewById(R.id.fragment_registerpopup_page_ticom);
        m_edtVK = view.findViewById(R.id.fragment_registerpopup_page_tivk);
        m_fab = view.findViewById(R.id.fragment_registerpopup_page_fab);
        m_Switch = view.findViewById(R.id.fragment_registerpopup_page_switch);

        //set EditText
        m_edttCount.setText(String.valueOf(0), TextView.BufferType.EDITABLE);
        m_edttCount.setCursorVisible(false);

        //set listener
        m_button_min.setOnClickListener(this);
        m_button_pl.setOnClickListener(this);
        m_fab.setOnClickListener(fabOnClickListener);

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

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.fragment_registerpopup_page_btnminus:
                button_minus();
                break;

            case R.id.fragment_registerpopup_page_btnplus:
                button_plus();
                break;

            default:

        }
    }

    private View.OnClickListener fabOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        }
    };

    private void button_minus(){
        if(m_iItems > 0){
            m_iItems--;
        }

        //set edittext
        m_edttCount.setText(String.valueOf(m_iItems), TextView.BufferType.EDITABLE);
    }

    private void button_plus(){
        m_iItems++;

        //set edittext
        m_edttCount.setText(String.valueOf(m_iItems), TextView.BufferType.EDITABLE);
    }
}
