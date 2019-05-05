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
import androidx.fragment.app.Fragment;

import com.example.dd.cashbox.R;

import java.text.DecimalFormat;


public class PopUpWindowCancelOKFragment extends DialogFragment implements View.OnClickListener {

    private Button m_btnOK;
    private Button m_btnCancel;
    private TextView m_tvText;
    private Context m_Context;
    private String m_strText = "";
    private String m_strTASK = "";
    private OnDialogCancelOkResultListener onDialogResultListener;
    private static PopUpWindowCancelOKFragment m_frag;

    public PopUpWindowCancelOKFragment() {
    }


    public static PopUpWindowCancelOKFragment newInstance() {
        m_frag = new PopUpWindowCancelOKFragment();

        return m_frag;
    }

    public interface OnDialogCancelOkResultListener {
        public abstract void onOkResult(String p_strTASK);
        public abstract void onCancelResult();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_popupwindowcancelok, container, false);

        //activity variables
        m_strText = getArguments().getString("TEXT");
        m_strTASK = getArguments().getString("TASK");

        //set UI
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        //set variables
        m_btnOK = view.findViewById(R.id.fragment_puco_btnok);
        m_btnCancel = view.findViewById(R.id.fragment_puco_btncancel);
        m_tvText = view.findViewById(R.id.fragment_puco_tv);
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
        OnDialogCancelOkResultListener listener = (OnDialogCancelOkResultListener)getActivity();
        switch(v.getId()){
            case R.id.fragment_puco_btnok:
                listener.onOkResult(m_strTASK);
                m_frag.dismiss();
                break;

            case R.id.fragment_puco_btncancel:
                listener.onCancelResult();
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
