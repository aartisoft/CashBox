package fragments;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.example.dd.cashbox.MS_CashRegisterInfo;
import com.example.dd.cashbox.Main;
import com.example.dd.cashbox.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import SQLite.SQLiteDatabaseHandler_Settings;
import SQLite.SQLiteDatabaseHandler_UserAccounts;
import global.GlobVar;
import objects.ObjBill;
import objects.ObjBillProduct;
import objects.ObjCategory;
import objects.ObjPrinter;
import objects.ObjProduct;
import objects.ObjUser;

public class AddNewUserDialogFragment extends DialogFragment {

    private EditText m_edtName;
    private FloatingActionButton m_fab;

    private int m_iItems = 0;
    private FragmentActivity m_Context;
    private static AddNewUserDialogFragment m_frag;

    public AddNewUserDialogFragment() {
    }

    public static AddNewUserDialogFragment newInstance() {
        m_frag = new AddNewUserDialogFragment();
        return m_frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_addnewuser, container, false);

        //activity variables
        m_Context = getActivity();

        //set UI
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        Toolbar toolbar = view.findViewById(R.id.fragment_addnewuser_tb);
        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);
        toolbar.setNavigationOnClickListener(tbOnClickListener);

        //set variables
        m_edtName = view.findViewById(R.id.fragment_addnewuser_tvname);
        m_fab = view.findViewById(R.id.fragment_addnewuser_fab);


        //set listener
        m_edtName.setOnTouchListener(nameOnTouchListener);
        m_edtName.setOnEditorActionListener(nameOnEditorActionListener);
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


    private View.OnTouchListener nameOnTouchListener = new View.OnTouchListener(){

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            m_edtName.setCursorVisible(true);
            return false;
        }
    };

    private TextView.OnEditorActionListener nameOnEditorActionListener = new TextView.OnEditorActionListener(){
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                m_edtName.setCursorVisible(false);
            }
            return false;
        }
    };


    private View.OnClickListener fabOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(!m_edtName.getText().toString().equals("")){
                ObjUser objUser = new ObjUser();
                objUser.setUserName(m_edtName.getText().toString());
                GlobVar.g_lstUser.add(objUser);

                //save to database
                SQLiteDatabaseHandler_Settings db_session = new SQLiteDatabaseHandler_Settings(m_Context);
                db_session.saveSettings();

                SQLiteDatabaseHandler_UserAccounts db_useraccounts = new SQLiteDatabaseHandler_UserAccounts(m_Context);
                db_useraccounts.addUser(objUser);
                db_useraccounts.updateUser();

                m_frag.dismiss();
            }
            else{
                Toast.makeText(m_Context, getResources().getString(R.string.src_BitteNamenAngeben), Toast.LENGTH_SHORT).show();
            }
        }
    };


    ///////////////////////////////////// METHODS ////////////////////////////////////////////////////////////////////////

}
