package objects;

import java.util.ArrayList;
import java.util.List;

public class ObjSession {
    private String m_strCashierName = "";
    private String m_strHostName = "";
    private String m_strPartyName = "";
    private String m_strPartyDate = "";

    public String getCashierName() {
        return m_strCashierName;
    }
    public String getHostName() {
        return m_strHostName;
    }
    public String getPartyName() {
        return m_strPartyName;
    }
    public String getPartyDate() {
        return m_strPartyDate;
    }

    public void setCashierName(String m_strCashierName) {
        this.m_strCashierName = m_strCashierName;
    }
    public void setHostName(String m_strHostName) {
        this.m_strHostName = m_strHostName;
    }
    public void setPartyName(String m_strPartyName) {
        this.m_strPartyName = m_strPartyName;
    }
    public void setPartyDate(String m_strPartyDate) {
        this.m_strPartyDate = m_strPartyDate;
    }


    public ObjSession(){
    }
}
