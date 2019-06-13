package fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.dd.cashbox.R;


public class PopUpWindowMainCashPrintBillFragment extends DialogFragment implements View.OnClickListener {

    private Button m_btnOK;
    private Button m_btnCancel;
    private TextView m_tvText;
    private Context m_Context;
    private String m_strText = "";
    private String m_strTASK = "";
    private OnDialogMainCashCancelOkResultListener onDialogResultListener;
    private static PopUpWindowMainCashPrintBillFragment m_frag;

    public PopUpWindowMainCashPrintBillFragment() {
    }


    public static PopUpWindowMainCashPrintBillFragment newInstance() {
        m_frag = new PopUpWindowMainCashPrintBillFragment();

        return m_frag;
    }

    public interface OnDialogMainCashCancelOkResultListener {
        public abstract void onMainCashOkResult(String p_strTASK);
        public abstract void onMainCashCancelResult();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_popupwindowmaincashprintbill, container, false);

        //activity variables
        m_strText = getArguments().getString("TEXT");
        m_strTASK = getArguments().getString("TASK");

        //set UI
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        //set variables
        m_btnOK = view.findViewById(R.id.fragment_pumcpb_btnok);
        m_btnCancel = view.findViewById(R.id.fragment_pumcpb_btncancel);
        m_tvText = view.findViewById(R.id.fragment_pumcpb_tv);
        m_Context = getContext();

        //set Listener
        m_btnOK.setOnClickListener(this);
        m_btnCancel.setOnClickListener(this);

        //set Text
        setText();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        OnDialogMainCashCancelOkResultListener listener = (OnDialogMainCashCancelOkResultListener)getActivity();
        switch(v.getId()){
            case R.id.fragment_pumcpb_btnok:
                listener.onMainCashOkResult(m_strTASK);
                m_frag.dismiss();
                break;

            case R.id.fragment_pumcpb_btncancel:
                listener.onMainCashCancelResult();
                m_frag.dismiss();
                break;

            default:

        }
    }

    //////////////////////////////// METHODS ////////////////////////////////////////////////////////////////////
    private void setText(){
        m_tvText.setText(m_strText);
    }
}
