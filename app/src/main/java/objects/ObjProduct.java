package objects;

public class ObjProduct {
    private String m_strName;
    private double m_dVK;
    private boolean m_bPawn;
    private double m_dPawn;
    private double m_dTax;
    private boolean m_bEnabled;
    private String m_strCategory;

    public ObjProduct(){
    }

    public String getName(){
        return this.m_strName;
    }
    public double getVK(){
        return this.m_dVK;
    }
    public boolean getbPawn(){
        return this.m_bPawn;
    }
    public double getPawn(){
        return this.m_dPawn;
    }
    public double getTax(){
        return this.m_dTax;
    }
    public boolean getEnabled(){
        return this.m_bEnabled;
    }
    public String getCategory(){
        return this.m_strCategory;
    }

    public void setName(String p_strName){
        this.m_strName = p_strName;
    }
    public void setVK(double p_dVK){
        this.m_dVK = p_dVK;
    }
    public void setbPAWN(boolean p_bPawn){
        this.m_bPawn = p_bPawn;
    }
    public void setPAWN(double p_dPawn){
        this.m_dPawn = p_dPawn;
    }
    public void setTax(double p_dTax){
        this.m_dTax = p_dTax;
    }
    public void setEnabled(boolean p_bEnabled){
        this.m_bEnabled = p_bEnabled;
    }
    public void set_Category(String p_strCategory){
        this.m_strCategory = p_strCategory;
    }
}
