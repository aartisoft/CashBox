package objects;

public class ObjProduct {
    private int m_iID;
    private String m_strName;
    private Float m_flEK;
    private Float m_flVK;
    private boolean m_bPawn;
    private Float m_flPawn;
    private boolean m_bEnabled;
    private String m_strCategory;

    public ObjProduct(){

    }

    public ObjProduct(int p_iID, String p_strName, Float p_flEK, Float p_flVK, boolean p_bPawn, Float p_flPawn, boolean p_bEnabled, String p_strCategory){
        this.m_iID = p_iID;
        this.m_strName = p_strName;
        this.m_flEK = p_flEK;
        this.m_flVK = p_flVK;
        this.m_bPawn = p_bPawn;
        this.m_flPawn = p_flPawn;
        this.m_bEnabled = p_bEnabled;
        this.m_strCategory = p_strCategory;
    }

    public int getID(){
        return this.m_iID;
    }
    public String getName(){
        return this.m_strName;
    }
    public Float getVK(){
        return this.m_flEK;
    }
    public Float getEK(){
        return this.m_flVK;
    }
    public boolean getbPawn(){
        return this.m_bPawn;
    }
    public Float getPawn(){
        return this.m_flPawn;
    }
    public boolean getEnabled(){
        return this.m_bEnabled;
    }
    public String getCategory(){
        return this.m_strCategory;
    }

    public void setID(int p_iID){
        this.m_iID = p_iID;
    }
    public void setName(String p_strName){
        this.m_strName = p_strName;
    }
    public void setEK(float p_flEK){
        this.m_flEK = p_flEK;
    }
    public void setVK(float p_flVK){
        this.m_flVK = p_flVK;
    }
    public void setbPAWN(boolean p_bPawn){
        this.m_bPawn = p_bPawn;
    }
    public void setPAWN(float p_flPawn){
        this.m_flPawn = p_flPawn;
    }
    public void setEnabled(boolean p_bEnabled){
        this.m_bEnabled = p_bEnabled;
    }
    public void set_Category(String p_strCategory){
        this.m_strCategory = p_strCategory;
    }
}
