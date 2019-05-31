package fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CompoundButton;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.example.dd.cashbox.MS_UserAccounts;
import com.example.dd.cashbox.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import SQLite.SQLiteDatabaseHandler_UserAccounts;
import global.GlobVar;
import objects.ObjUser;

public class EditUserDialogFragment extends DialogFragment {

    private SwitchCompat m_SwitchActive;
    private FloatingActionButton m_fab;
    private boolean m_bActive = false;
    private int m_iPositionUser;
    private FragmentActivity m_Context;
    private static EditUserDialogFragment m_frag;

    public EditUserDialogFragment() {
    }

    public static EditUserDialogFragment newInstance() {
        m_frag = new EditUserDialogFragment();
        return m_frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_edituser, container, false);

        //activity variables
        m_Context = getActivity();
        m_iPositionUser = getArguments().getInt("USER", -1);

        //set UI
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        Toolbar toolbar = view.findViewById(R.id.fragment_edituser_tb);
        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);
        toolbar.setNavigationOnClickListener(tbOnClickListener);

        //set variables
        m_SwitchActive = view.findViewById(R.id.fragment_edituser_switch);
        m_fab = view.findViewById(R.id.fragment_edituser_fab);


        //set listener
        m_SwitchActive.setOnCheckedChangeListener(activeOnCheckedChangeListener);
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

    private CompoundButton.OnCheckedChangeListener activeOnCheckedChangeListener = new CompoundButton.OnCheckedChangeListener(){
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            m_bActive = isChecked;
        }
    };



    private View.OnClickListener fabOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
           for(ObjUser objUser : GlobVar.g_lstUser){
               objUser.setActive(false);
           }

            if(m_bActive){
                GlobVar.g_lstUser.get(m_iPositionUser).setActive(true);
                GlobVar.g_ObjSession.setCashierName(GlobVar.g_lstUser.get(m_iPositionUser).getUserName());
            }

            //update database
            SQLiteDatabaseHandler_UserAccounts db_useraccounts = new SQLiteDatabaseHandler_UserAccounts(m_Context);
            db_useraccounts.updateUser();

            ((MS_UserAccounts)m_Context).initList();
            m_frag.dismiss();
        }
    };


    ///////////////////////////////////// METHODS ////////////////////////////////////////////////////////////////////////

}
