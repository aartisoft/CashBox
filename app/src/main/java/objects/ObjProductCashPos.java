package objects;

public class ObjProductCashPos {
    private String m_strName;
    private String m_strVK;
    private String m_strInfo;


    public ObjProductCashPos(){
    }

    public String getName(){
        return this.m_strName;
    }
    public String getVK(){
        return this.m_strVK;
    }
    public String getInfo(){
        return this.m_strInfo;
    }


    public void setName(String p_strName){
        this.m_strName = p_strName;
    }
    public void setVK(String p_strVK){
        this.m_strVK = p_strVK;
    }
    public void setInfo(String p_strInfo){
        this.m_strInfo = p_strInfo;
    }

}
