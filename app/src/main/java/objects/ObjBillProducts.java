package objects;

public class ObjBillProducts {
    private ObjProduct m_objProduct;
    private int m_iQuantity;
    private String m_strCategory;
    private ObjPrinter m_objPrinter;
    private boolean m_bPrinted;
    private boolean m_bCanceled;
    private boolean m_bPaid;

    //getter
    public ObjProduct getProduct(){
        return this.m_objProduct;
    }
    public int getQuantity(){
        return this.m_iQuantity;
    }
    public String getCategory(){
        return this.m_strCategory;
    }
    public ObjPrinter getPrinter(){
        return this.m_objPrinter;
    }
    public boolean getPrinted(){
        return this.m_bPrinted;
    }
    public boolean getCanceled(){
        return this.m_bCanceled;
    }
    public boolean getPaid(){
        return this.m_bPaid;
    }

    //setter
    public void setProductName(ObjProduct p_objProduct){
        this.m_objProduct = p_objProduct;
    }
    public void setQuantity(int p_iQuantity){
        this.m_iQuantity = p_iQuantity;
    }
    public void setCategory(String p_strCategory){
        this.m_strCategory = p_strCategory;
    }
    public void setPrinter(ObjPrinter p_objPrinter){
        this.m_objPrinter = p_objPrinter;
    }
    public void setPrinted(boolean p_bPrinted){
        this.m_bPrinted = p_bPrinted;
    }
    public void setCanceled(boolean p_bCanceled){
        this.m_bCanceled = p_bCanceled;
    }
    public void setPaid(boolean p_bPaid){
        this.m_bPaid = p_bPaid;
    }
}
