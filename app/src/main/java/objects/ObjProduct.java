package objects;

public class ObjProduct {
    private String m_strName;
    private Float m_flEK;
    private Float m_flVK;
    private boolean m_bPawn;
    private Float m_flPawn;
    private boolean m_bEnabled;

    ObjProduct(String p_strName, Float p_flEK, Float p_flVK, boolean p_bPawn, Float p_flPawn, boolean p_bEnabled){
        this.m_strName = p_strName;
        this.m_flEK = p_flEK;
        this.m_flVK = p_flVK;
        this.m_bPawn = p_bPawn;
        this.m_flPawn = p_flPawn;
        this.m_bEnabled = p_bEnabled;
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
}
