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

import com.example.dd.cashbox.R;

import java.text.DecimalFormat;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;


public class PopUpWindowOkRetoureStornoFragment extends DialogFragment implements View.OnClickListener {

    private Button m_btnCancel;
    private Button m_btnOK;
    private TextView m_tvText;
    private double m_dCash = 0;
    private Context m_Context;
    private static PopUpWindowOkRetoureStornoFragment m_frag;

    public PopUpWindowOkRetoureStornoFragment() {
    }


    public static PopUpWindowOkRetoureStornoFragment newInstance() {
        m_frag = new PopUpWindowOkRetoureStornoFragment();
        return m_frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_popupwindowcancelok, container, false);

        //activity variables
        m_dCash = getArguments().getDouble("CASH");

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
        Fragment mParentFragment = (ViewPagerRetoureStornoFragment) getParentFragment();
        switch(v.getId()){
            case R.id.fragment_puco_btnok:
                ((ViewPagerRetoureStornoFragment) mParentFragment).raiseCloseDialog();
                m_frag.dismiss();
                break;

            case R.id.fragment_puco_btncancel:
                ((ViewPagerRetoureStornoFragment) mParentFragment).raiseClear();
                m_frag.dismiss();
                break;

            default:

        }
    }

    private void setText(){
        //product has been canceled
        if(m_dCash == 0){
            String strText = getResources().getString(R.string.src_ArtikelWerdenStorniert) + "\n\n";
            strText += getResources().getString(R.string.src_BitteBestaetigenSieDenVorgang);
            m_tvText.setText(strText);
        }
        //product has been returned
        else{
            DecimalFormat df = new DecimalFormat("0.00");
            String strText = getResources().getString(R.string.src_ArtikelWerdenStorniert) + "\n";
            strText += getResources().getString(R.string.src_KundeBekommtSummeXZurueck) + "\n\n";
            strText += getResources().getString(R.string.src_BitteBestaetigenSieDenVorgang);
            String strVK = df.format(m_dCash);
            strText = strText.replace("{0}", strVK);
            m_tvText.setText(strText);
        }
    }
}
