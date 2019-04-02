package objects;

public class ObjBillProduct {
    private ObjProduct m_objProduct;
    private int m_iQuantity;
    private String m_strCategory;
    private ObjPrinter m_objPrinter;
    private int m_iPrinted;
    private int m_iCanceled;
    private int m_iPaid;
    private int m_iReturned;
    private boolean m_bSqlSaved;
    private boolean m_bSqlChanged;

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
    public int getPrinted(){
        return this.m_iPrinted;
    }
    public int getCanceled(){
        return this.m_iCanceled;
    }
    public int getPaid(){
        return this.m_iPaid;
    }
    public int getReturned(){
        return this.m_iReturned;
    }
    public boolean getSqlSaved() {
        return m_bSqlSaved;
    }
    public boolean getqlChanged() {
        return m_bSqlChanged;
    }

    //setter
    public void setProduct(ObjProduct p_objProduct){
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
    public void setPrinted(int p_bPrinted){
        this.m_iPrinted = p_bPrinted;
    }
    public void setCanceled(int p_bCanceled){
        this.m_iCanceled = p_bCanceled;
    }
    public void setPaid(int p_bPaid){
        this.m_iPaid = p_bPaid;
    }
    public void setReturned(int p_iReturned){
        this.m_iReturned = p_iReturned;
    }
    public void setSqlSaved(boolean m_bSqlSaved) {
        this.m_bSqlSaved = m_bSqlSaved;
    }
    public void setSqlChanged(boolean m_bSqlChanged) {
        this.m_bSqlChanged = m_bSqlChanged;
    }
}
