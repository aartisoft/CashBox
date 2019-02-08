package global;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

import global.PrinterStruct;

public abstract class PrinterList extends Application {



    public static List<PrinterStruct> m_lstPrinter = new ArrayList<PrinterStruct>();

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
