package objects;

public class ObjPrinterSearch {
    private String m_strDeviceName;
    private int m_iDeviceType;
    private String m_strTarget;
    private String m_strIpAddress;
    private String m_strMacAddress;
    private String m_strBdAddress;

    private boolean m_bChecked = false;

    public ObjPrinterSearch(String p_strDeviceName, int p_iDeviceType, String p_strTarget, String p_strIpAddress
                            , String p_strMacAddress, String p_strBdAddress, boolean p_bChecked) {
        this.m_strDeviceName = p_strDeviceName;
        this.m_iDeviceType = p_iDeviceType;
        this.m_strTarget = p_strTarget;
        this.m_strIpAddress = p_strIpAddress;
        this.m_strMacAddress = p_strMacAddress;
        this.m_strBdAddress = p_strBdAddress;
        this.m_bChecked = p_bChecked;
    }

    public String getDeviceName(){
        return this.m_strDeviceName;
    }
    public int getDeviceType(){
        return this.m_iDeviceType;
    }
    public String getTarget(){
        return this.m_strTarget;
    }
    public String getIpAddress(){
        return this.m_strIpAddress;
    }
    public String getMacAddress(){
        return this.m_strMacAddress;
    }
    public String getBdAddress(){
        return this.m_strBdAddress;
    }
    public boolean isChecked() {
        return m_bChecked;
    }

    public void setDeviceName(String p_strDeviceName){
        this.m_strDeviceName = p_strDeviceName;
    }
    public void setDeviceType(int p_iDeviceType){
        this.m_iDeviceType = p_iDeviceType;
    }
    public void setTarget(String p_strTarget){
        this.m_strTarget = p_strTarget;
    }
    public void setIpAdress(String p_strIpAddress){
        this.m_strIpAddress = p_strIpAddress;
    }
    public void setMacAddress(String p_strMacAddress){
        this.m_strMacAddress = p_strMacAddress;
    }
    public void setBdAddress(String p_strBdAddresst){
        this.m_strBdAddress = p_strBdAddresst;
    }
    public void setChecked(boolean p_bChecked) {
        this.m_bChecked = p_bChecked;
    }
}