package objects;

import java.util.ArrayList;
import java.util.List;

public class ObjBill {
    private int m_iBillNr;
    private int m_iTable;
    private String m_strCashierName;
    private String m_strBillingDate;
    private double m_dTip = 0.0;
    public List<ObjBillProduct> m_lstProducts = new ArrayList<ObjBillProduct>();
    private boolean m_bSqlChanged;
    private boolean m_bClosed = false;

    //getter
    public int getBillNr(){
        return this.m_iBillNr;
    }
    public int getTable() {
        return m_iTable;
    }
    public String getCashierName(){
        return this.m_strCashierName;
    }
    public String getBillingDate(){
        return this.m_strBillingDate;
    }
    public double getTip(){
        return this.m_dTip;
    }
    public boolean getClosed() {
        return m_bClosed;
    }
    public boolean getSqlChanged() {
        return m_bSqlChanged;
    }


    //setter
    public void setBillNr(int p_iBillNr){
        this.m_iBillNr = p_iBillNr;
    }
    public void setTable(int m_iTable) {
        this.m_iTable = m_iTable;
    }
    public void setCashierName(String p_strCashierName){
        this.m_strCashierName = p_strCashierName;
    }
    public void setBillingDate(String p_strBillingDate){
        this.m_strBillingDate = p_strBillingDate;
    }
    public void setTip(double p_dTip){
        this.m_dTip = p_dTip;
    }
    public void setClosed(boolean m_bClosed) {
        this.m_bClosed = m_bClosed;
    }
    public void setSqlChanged(boolean m_bSqlChanged) {
        this.m_bSqlChanged = m_bSqlChanged;
    }
}
