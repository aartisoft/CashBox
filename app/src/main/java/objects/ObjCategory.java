package objects;

import java.util.ArrayList;
import java.util.List;

public class ObjCategory {
    private String m_strName;
    private int m_iColor;
    private ObjPrinter m_Printer;
    private boolean m_bEnabled;
    private List<ObjProduct> m_lstProduct = new ArrayList<ObjProduct>();

    public ObjCategory(){

    }

    ObjCategory(String p_strName, int p_iColor, ObjPrinter p_Printer, boolean p_bEnabled, List<ObjProduct> p_lstProduct){
        this.m_strName = p_strName;
        this.m_iColor = p_iColor;
        this.m_Printer = p_Printer;
        this.m_bEnabled = p_bEnabled;
        this.m_lstProduct = p_lstProduct;
    }

    public String getName(){
        return this.m_strName;
    }
    public int getProdColor(){
        return this.m_iColor;
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

    public void setName(String p_strName){
        this.m_strName = p_strName;
    }
    public void setProdColor(int p_iColor){
        this.m_iColor = p_iColor;
    }
    public void setPrinter(ObjPrinter p_Printer){
        this.m_Printer = p_Printer;
    }
    public void setEnabled(boolean p_bEnabled){
        this.m_bEnabled = p_bEnabled;
    }
    public void setProductList(List<ObjProduct> p_lstProduct){
        this.m_lstProduct = p_lstProduct;
    }
}
