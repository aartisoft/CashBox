package objects;

import java.util.Date;
import java.util.List;

public class ObjBill {
    private int m_iBillNr;
    private String m_strCashierName;
    private Date m_BillingDate;
    public List<ObjBillProduct> m_lstProducts;

    //getter
    public int getBillNr(){
        return this.m_iBillNr;
    }
    public String getCashierName(){
        return this.m_strCashierName;
    }
    public Date getBillingDate(){
        return this.m_BillingDate;
    }


    //setter
    public void setBillNr(int p_iBillNr){
        this.m_iBillNr = p_iBillNr;
    }
    public void setCashierName(String p_strCashierName){
        this.m_strCashierName = p_strCashierName;
    }
    public void setBillingDate(Date p_BillingDate){
        this.m_BillingDate = p_BillingDate;
    }
}
