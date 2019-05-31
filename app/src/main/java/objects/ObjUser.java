package objects;

public class ObjUser {

    public ObjUser(){
    }

    private String m_strUserName = "";
    private boolean m_bActive = false;
    private boolean m_bDelete = false;
    private boolean m_bChecked = false;

    public String getUserName() {
        return m_strUserName;
    }
    public boolean isActive() {
        return m_bActive;
    }
    public boolean isDelete() {
        return m_bDelete;
    }
    public boolean isChecked() {
        return m_bChecked;
    }


    public void setUserName(String p_strUserName) {
        this.m_strUserName = p_strUserName;
    }
    public void setActive(boolean p_bActive) {
        this.m_bActive = p_bActive;
    }
    public void setDelete(boolean p_bDelete) {
        this.m_bDelete = p_bDelete;
    }
    public void setChecked(boolean p_bChecked) {
        this.m_bChecked = p_bChecked;
    }
}
