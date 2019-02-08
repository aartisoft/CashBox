package objects;

import android.widget.CheckBox;

public class ObjPrinterSearch {
    public String m_strName;
    public String m_strTarget;
    private boolean m_bChecked = false;

    public ObjPrinterSearch(String p_strName, String p_strTarget, boolean p_bChecked) {
        this.m_strName = p_strName;
        this.m_strTarget = p_strTarget;
        this.m_bChecked = p_bChecked;
    }

    public String getName(){
        return this.m_strName;
    }
    public String getTarget(){
        return this.m_strTarget;
    }
    public boolean isChecked() {
        return m_bChecked;
    }
    public void setName(String p_strName){
        this.m_strName = p_strName;
    }
    public void setTarget(String p_strTarget){
        this.m_strTarget = p_strTarget;
    }
    public void setChecked(boolean p_bChecked) {
        this.m_bChecked = p_bChecked;
    }
}
