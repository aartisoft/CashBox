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
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;


public class PopUpWindowOkFragment extends DialogFragment implements View.OnClickListener {

    private Button m_button;
    private Button m_button_min;
    private Button m_button_pl;
    private EditText m_edttCount;
    private TextView m_tvText;
    private PopUpWIndowOkListener m_listener;
    private int m_iCash = 0;
    private Context m_Context;
    private static PopUpWindowOkFragment m_frag;

    public PopUpWindowOkFragment() {
    }

    public interface PopUpWIndowOkListener {
        void onFinishRetoureDialog();
    }

    public static PopUpWindowOkFragment newInstance() {
        m_frag = new PopUpWindowOkFragment();
        return m_frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_popupwindowok, container, false);

        //activity variables
        m_iCash = getArguments().getInt("CASH", 0);

        //set UI
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        //set variables
        m_button = view.findViewById(R.id.fragment_puwok_button);
        m_tvText = view.findViewById(R.id.fragment_puwok_tv);
        m_Context = getContext();

        //set Listener
        m_button.setOnClickListener(this);

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
        m_frag.dismiss();
    }

    private void setText(){
        //product has been canceled
        if(m_iCash == 0){
            String strText = getResources().getString(R.string.src_KundeHatArtikelNochNichtBezahlt);
            m_tvText.setText(strText);
        }
        //product has been returned
        else{
            String strText = getResources().getString(R.string.src_KundeHatArtikelNochNichtBezahlt);
            m_tvText.setText(strText);
        }
    }
}
