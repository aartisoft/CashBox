package fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.dd.cashbox.Main;
import com.example.dd.cashbox.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import global.GlobVar;
import objects.ObjCategory;
import objects.ObjPrinter;


public class PopUpWindowPrintBillFragment extends DialogFragment implements View.OnClickListener {

    private Button m_btnPrint;
    private Button m_btnCancel;
    private SwitchCompat m_SwitchEcBill;
    private Spinner m_Spinner_Printer;
    private Context m_Context;
    private static PopUpWindowPrintBillFragment m_frag;

    public PopUpWindowPrintBillFragment() {
    }


    public static PopUpWindowPrintBillFragment newInstance() {
        m_frag = new PopUpWindowPrintBillFragment();

        return m_frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_printbill, container, false);

        //activity variables

        //set UI
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        //set variables
        m_Context = getContext();
        m_btnPrint = view.findViewById(R.id.fragment_printbill_btnprint);
        m_btnCancel = view.findViewById(R.id.fragment_printbill_btncancel);
        m_SwitchEcBill = view.findViewById(R.id.fragment_printbill_switch);
        m_Spinner_Printer = view.findViewById(R.id.fragment_printbill_spinnerprinter);

        //set spinner printer
        setPrinter();

        //set Listener
        m_btnPrint.setOnClickListener(this);
        m_btnCancel.setOnClickListener(this);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.fragment_printbill_btnprint:
                //print bill
                ((Main) getActivity()).addPrintJobBill(m_SwitchEcBill.isChecked(), getPrinter());

                m_frag.dismiss();
                break;

            case R.id.fragment_printbill_btncancel:
                m_frag.dismiss();
                break;

            default:

        }
    }

    //////////////////////////////// METHODS ////////////////////////////////////////////////////////////////////
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
