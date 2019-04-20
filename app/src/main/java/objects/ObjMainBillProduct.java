package objects;

public class ObjMainBillProduct {
    private ObjProduct m_objProduct;
    private int m_iQuantity;
    private int m_iPrinted;
    private double m_dVK;

    //getter
    public ObjProduct getProduct(){
        return this.m_objProduct;
    }
    public int getQuantity(){
        return this.m_iQuantity;
    }
    public int getPrinted(){
        return this.m_iPrinted;
    }
    public double getVK(){
        return this.m_dVK;
    }

    //setter
    public void setProduct(ObjProduct p_objProduct){
        this.m_objProduct = p_objProduct;
    }
    public void setQuantity(int p_iQuantity){
        this.m_iQuantity = p_iQuantity;
    }
    public void setPrinted(int p_bPrinted){
        this.m_iPrinted = p_bPrinted;
    }
    public void setVK(double p_dVK){
        this.m_dVK = p_dVK;
    }
}
