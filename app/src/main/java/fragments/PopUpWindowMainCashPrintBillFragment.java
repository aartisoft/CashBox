package fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.dd.cashbox.R;

import java.util.ArrayList;
import java.util.List;

import global.GlobVar;
import objects.ObjPrinter;


public class PopUpWindowMainCashPrintBillFragment extends DialogFragment implements View.OnClickListener {

    private Button m_btnOK;
    private Button m_btnCancel;
    private TextView m_tvText;
    private Spinner m_Spinner_Printer;
    private CheckBox m_cbEcBill;
    private CheckBox m_cbNormalBill;
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
        public abstract void onMainCashOkResult(String p_strTASK, boolean p_bprint, ObjPrinter p_objPrinter, boolean p_bEcBill);
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
        m_Spinner_Printer = view.findViewById(R.id.fragment_pumcpb_spinnerprinter);
        m_cbEcBill = view.findViewById(R.id.fragment_pumcpb_ecbill_cb);
        m_cbNormalBill = view.findViewById(R.id.fragment_pumcpb_normalbill_cb);
        m_Context = getContext();

        //set Text
        setText();

        //set spinner printer
        setPrinter();

        //set Listener
        m_btnOK.setOnClickListener(this);
        m_btnCancel.setOnClickListener(this);

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

                //get checkboxes
                boolean bPrint = false;
                boolean bEcBill = false;
                if(m_cbEcBill.isChecked() || m_cbNormalBill.isChecked()){
                    bPrint = true;

                    if(m_cbEcBill.isSelected()){
                        bEcBill = true;
                    }
                }

                listener.onMainCashOkResult(m_strTASK, bPrint, getPrinter(), bEcBill);
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

    private ObjPrinter getPrinter(){
        //get object printer
        ObjPrinter foundPrinter = new ObjPrinter();
        foundPrinter = null;
        String spinnerprinter = m_Spinner_Printer.getSelectedItem().toString();
        String macadress = spinnerprinter.substring(spinnerprinter.indexOf(":") + 1);

        for (ObjPrinter printer : GlobVar.g_lstPrinter) {
            if (printer.getMacAddress().equals(macadress)) {
                foundPrinter = printer;
                break;
            }
        }

        return foundPrinter;
    }

    private void setPrinter(){
        m_Spinner_Printer.setPrompt(getResources().getString(R.string.src_DruckerAuswaehlen));

        List<String> lstPrinter = new ArrayList<>();
        lstPrinter.add(getResources().getString(R.string.src_KeinenDruckerAusgewählt));

        if(!GlobVar.g_lstPrinter.isEmpty()){
            for(ObjPrinter printer : GlobVar.g_lstPrinter){
                lstPrinter.add(printer.getDeviceName() + " - MAC:" + printer.getMacAddress());
            }
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(m_Context, android.R.layout.simple_spinner_dropdown_item, lstPrinter);
            m_Spinner_Printer.setAdapter(dataAdapter);
        }
        else{
            lstPrinter.add(getResources().getString(R.string.src_KeineDruckerVorhanden));
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(m_Context, android.R.layout.simple_spinner_item, lstPrinter);
            m_Spinner_Printer.setAdapter(dataAdapter);
        }
    }
}
