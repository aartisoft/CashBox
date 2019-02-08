package global;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

import objects.ObjPrinter;

public abstract class PrinterList extends Application {

    public static List<ObjPrinter> m_lstPrinter = new ArrayList<ObjPrinter>();

}
