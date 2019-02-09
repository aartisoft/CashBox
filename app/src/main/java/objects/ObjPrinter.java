package objects;

public class ObjPrinter {
    private String m_strName;
    private String m_strTargetShown;
    private String m_strTarget;
    private String m_strCategory;


    public void setPrinter(String p_strName, String p_strTargetShown, String p_strTarget, String p_strCategory){

        m_strName = p_strName;
        m_strTarget = p_strTarget;
        m_strTargetShown = p_strTargetShown;
        m_strCategory = p_strCategory;
    }

    public String getName(){
        return this.m_strName;
    }
    public String getTargetShown(){
        return this.m_strTargetShown;
    }
    public String getTarget(){
        return this.m_strTarget;
    }
    public String getCategory() {
        return m_strCategory;
    }
    public void setName(String p_strName){
        this.m_strName = p_strName;
    }
    public void setTargetShown(String p_strTargetShown){
        this.m_strTargetShown = p_strTargetShown;
    }
    public void setTarget(String p_strTarget){
        this.m_strTarget = p_strTarget;
    }
    public void setCategory(String p_strCategory) {
        this.m_strCategory = p_strCategory;
    }
}