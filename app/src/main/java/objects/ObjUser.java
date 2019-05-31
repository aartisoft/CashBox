package objects;

public class ObjUser {

    public ObjUser(){
    }

    private String m_strUserName = "";
    private boolean m_bActive = false;

    public String getUserName() {
        return m_strUserName;
    }
    public boolean isActive() {
        return m_bActive;
    }


    public void setUserName(String p_strUserName) {
        this.m_strUserName = p_strUserName;
    }
    public void setActive(boolean p_bChecked) {
        this.m_bActive = p_bChecked;
    }
}
