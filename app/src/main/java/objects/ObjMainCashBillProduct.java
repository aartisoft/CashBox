package objects;

public class ObjMainCashBillProduct {
    private ObjProduct m_objProduct;
    private int m_iQuantity;
    private String m_strAddInfo = "";
    private double m_dSum;

    //getter
    public ObjProduct getProduct(){
        return this.m_objProduct;
    }
    public int getQuantity(){
        return this.m_iQuantity;
    }
    public String getAddInfo() {
        return m_strAddInfo;
    }
    public double getSum(){
        return this.m_dSum;
    }

    //setter
    public void setProduct(ObjProduct p_objProduct){
        this.m_objProduct = p_objProduct;
    }
    public void setQuantity(int p_iQuantity){
        this.m_iQuantity = p_iQuantity;
    }
    public void setAddInfo(String m_strAddInfo) {
        this.m_strAddInfo = m_strAddInfo;
    }
    public void setSum(double p_dVK){
        this.m_dSum = p_dVK;
    }
}
