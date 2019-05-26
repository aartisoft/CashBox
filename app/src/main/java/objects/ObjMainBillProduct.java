package objects;

public class ObjMainBillProduct {
    private ObjProduct m_objProduct;
    private int m_iQuantity;
    private int m_iPaid;
    private int m_iPrinted;
    private int m_iCanceled;
    private int m_iReturned;
    private double m_dVK;

    //getter
    public ObjProduct getProduct(){
        return this.m_objProduct;
    }
    public int getQuantity(){
        return this.m_iQuantity;
    }
    public int getPaid(){
        return this.m_iPaid;
    }
    public int getPrinted(){
        return this.m_iPrinted;
    }
    public int getCanceled(){
        return this.m_iCanceled;
    }
    public int getReturned(){
        return this.m_iReturned;
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
    public void setPaid(int p_bPaid){
        this.m_iPaid = p_bPaid;
    }
    public void setPrinted(int p_bPrinted){
        this.m_iPrinted = p_bPrinted;
    }
    public void setCanceled(int p_bCanceled){
        this.m_iCanceled = p_bCanceled;
    }
    public void setReturned(int p_bReturned){
        this.m_iReturned = p_bReturned;
    }
    public void setVK(double p_dVK){
        this.m_dVK = p_dVK;
    }
}
