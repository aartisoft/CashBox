package objects;

public class ObjUser {

    public ObjUser(){
    }

    private String m_strUserName = "";
    private boolean m_bChecked = false;

    public String getUserName() {
        return m_strUserName;
    }
    public boolean isChecked() {
        return m_bChecked;
    }


    public void setUserName(String p_strUserName) {
        this.m_strUserName = p_strUserName;
    }
    public void setChecked(boolean p_bChecked) {
        this.m_bChecked = p_bChecked;
    }
}
