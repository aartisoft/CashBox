package objects;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class ObjPrintJob {

    private Context m_Context;
    private ObjPrinter m_printer;
    private String[] m_arrBillText = new String[7];
    private boolean m_bBon = false;
    private boolean m_bBonExtra = false;
    private boolean m_bBonPawn = false;
    private boolean m_bNormalBill = false;
    private boolean m_bEcBill = false;
    private boolean m_bPrinted = false;
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
    public boolean getbBon() {
        return m_bBon;
    }
    public boolean getbBonExtra() {
        return m_bBonExtra;
    }
    public boolean getbBonPawn() {
        return m_bBonPawn;
    }
    public boolean getbNormalBill() {
        return m_bNormalBill;
    }
    public boolean getbEcBill() {
        return m_bEcBill;
    }
    public boolean getPrinted() {
        return m_bPrinted;
    }

    //setter
    public void setContext(Context p_Context) {
        this.m_Context = p_Context;
    }
    public void setPrinter(ObjPrinter p_printer) {
        this.m_printer = p_printer;
    }
    public void setBillText(String[] p_arrBillText) {
        this.m_arrBillText = p_arrBillText;
    }
    public void setbBon(boolean p_bBon) {
        this.m_bBon = p_bBon;
    }
    public void setbBonExtra(boolean p_bBonExtra) {
        this.m_bBonExtra = p_bBonExtra;
    }
    public void setbBonPawn(boolean p_bBonPawn) {
        this.m_bBonPawn = p_bBonPawn;
    }
    public void setbNormalBill(boolean p_bNormalBill) {
        this.m_bNormalBill = p_bNormalBill;
    }
    public void setbEcBill(boolean p_bEcBill) {
        this.m_bEcBill = p_bEcBill;
    }
    public void setPrinted(boolean p_bPrinted) {
        this.m_bPrinted = p_bPrinted;
    }
}
