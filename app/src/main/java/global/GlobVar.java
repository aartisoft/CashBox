package global;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

import objects.ObjCategory;
import objects.ObjPrinter;

public abstract class GlobVar extends Application {

    public static List<ObjPrinter> m_lstPrinter = new ArrayList<ObjPrinter>();
    public static String m_strBedienername = "Bedienername";
    public static int m_iTables = 0;
    public static List<ObjCategory> m_lstCategroy = new ArrayList<ObjCategory>();

}
