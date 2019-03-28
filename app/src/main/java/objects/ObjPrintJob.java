package objects;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class ObjPrintJob {

    private Context m_Context;
    private ObjPrinter m_printer;
    private String[] m_arrBillText = new String[10];
    public List<String[]> g_lstBillText = new ArrayList<>();

    //constructor
    public ObjPrintJob(){}

    //getter
    public Context getContext() {
        return m_Context;
    }
    public ObjPrinter getPrinter() {
        return m_printer;
    }
    public String[] getBillText() {
        return m_arrBillText;
    }

    //setter
    public void setContext(Context m_Context) {
        this.m_Context = m_Context;
    }
    public void setPrinter(ObjPrinter m_printer) {
        this.m_printer = m_printer;
    }
    public void setBillText(String[] m_arrBillText) {
        this.m_arrBillText = m_arrBillText;
    }
}
