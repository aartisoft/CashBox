package objects;

import java.util.List;

public class ObjBill {
    private int m_iBillNr;
    private String m_strCashierName;
    private String m_strBillingDate;
    public List<ObjBillProduct> m_lstProducts;

    //getter
    public int getBillNr(){
        return this.m_iBillNr;
    }
    public String getCashierName(){
        return this.m_strCashierName;
    }
    public String getBillingDate(){
        return this.m_strBillingDate;
    }


    //setter
    public void setBillNr(int p_iBillNr){
        this.m_iBillNr = p_iBillNr;
    }
    public void setCashierName(String p_strCashierName){
        this.m_strCashierName = p_strCashierName;
    }
    public void setBillingDate(String p_strBillingDate){
        this.m_strBillingDate = p_strBillingDate;
    }
}
