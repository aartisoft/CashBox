package global;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

import objects.ObjPrinter;

public abstract class PrinterList extends Application {



    public static List<ObjPrinter> m_lstPrinter = new ArrayList<ObjPrinter>();

    /*public void addEntryPrinterList(String p_strName, String p_strTarget, String p_strCategory){
        Printer printer = new Printer();
        printer.m_strName = p_strName;
        printer.m_strTarget = p_strTarget;
        printer.m_strCategory = p_strCategory;

        m_lstPrinter.add(printer);
    }
    public void delEntryPrinterList(){
        //m_lstPrinter.
    }

    public List<Printer> getPrinterList(){
        return this.m_lstPrinter;
    }*/
}
