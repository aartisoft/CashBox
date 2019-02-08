package global;

import android.app.Application;

import java.util.List;

public final class PrinterList extends Application {

    private class Printer{
        private String m_strName;
        private String m_strTarget;
        private String m_strCategory;
    }

    private static List<Printer> m_lstPrinter;

    public void setPrinterList(String p_strName, String p_strTarget, String p_strCategory){
        Printer printer = new Printer();
        printer.m_strName = p_strName;
        printer.m_strTarget = p_strTarget;
        printer.m_strCategory = p_strCategory;

        m_lstPrinter.add(printer);
    }

    public List<Printer> getPrinterList(){
        return this.m_lstPrinter;
    }
}
