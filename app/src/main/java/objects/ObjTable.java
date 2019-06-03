package objects;

import java.util.ArrayList;
import java.util.List;

public class ObjTable {
    private String m_strTableName;
    public List<ObjBill> g_lstBills = new ArrayList<ObjBill>();

    //getter
    public String getTableName(){
        return this.m_strTableName;
    }

    //setter
    public void setTableName(String p_strTableName){
        this.m_strTableName = p_strTableName;
    }
}
