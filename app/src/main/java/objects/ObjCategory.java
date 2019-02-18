package objects;

import java.util.List;

public class ObjCategory {
    private String m_strName;
    private String m_strColor;
    private ObjPrinter m_Printer;
    private boolean m_bEnabled;
    private List<ObjProduct> m_lstProduct;

    ObjCategory(String p_strName, String p_strColor, ObjPrinter p_Printer, boolean p_bEnabled, List<ObjProduct> p_lstProduct){
        this.m_strName = p_strName;
        this.m_strColor = p_strColor;
        this.m_Printer = p_Printer;
        this.m_bEnabled = p_bEnabled;
        this.m_lstProduct = p_lstProduct;
    }

    public String getName(){
        return this.m_strName;
    }
    public String getProdColor(){
        return this.m_strColor;
    }
    public ObjPrinter getPrinter(){
        return this.m_Printer;
    }
    public boolean getEnabled(){
        return this.m_bEnabled;
    }
    public List<ObjProduct> getListProduct(){
        return this.m_lstProduct;
    }
}
