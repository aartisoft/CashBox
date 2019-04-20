package objects;

public class ObjBillProduct {
    private ObjProduct m_objProduct;
    private double m_dVK;
    private String m_strAddInfo = "";
    private String m_strCategory;
    private ObjPrinter m_objPrinter;
    private boolean m_bPrinted;
    private boolean m_bCanceled;
    private boolean m_bPaid;
    private boolean m_bReturned;
    private boolean m_bSqlSaved;
    private boolean m_bSqlChanged;

    //getter
    public ObjProduct getProduct(){
        return this.m_objProduct;
    }
    public double getVK(){
        return this.m_dVK;
    }
    public String getAddInfo() {
        return m_strAddInfo;
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
    public boolean getReturned(){
        return this.m_bReturned;
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
    public void setVK(double p_dVK){
        this.m_dVK = p_dVK;
    }
    public void setAddInfo(String m_strAddInfo) {
        this.m_strAddInfo = m_strAddInfo;
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
    public void setReturned(boolean p_iReturned){
        this.m_bReturned = p_iReturned;
    }
    public void setSqlSaved(boolean m_bSqlSaved) {
        this.m_bSqlSaved = m_bSqlSaved;
    }
    public void setSqlChanged(boolean m_bSqlChanged) {
        this.m_bSqlChanged = m_bSqlChanged;
    }
}
